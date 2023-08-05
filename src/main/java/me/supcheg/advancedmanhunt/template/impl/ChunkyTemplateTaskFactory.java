package me.supcheg.advancedmanhunt.template.impl;

import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.TemplateTaskFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.DeletingFileVisitor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.Selection;
import org.popcraft.chunky.platform.BukkitWorld;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@CustomLog
public class ChunkyTemplateTaskFactory implements TemplateTaskFactory {
    private final ContainerAdapter containerAdapter;
    private final EntityRepository<Template, String> templateRepository;
    private final Chunky chunky;
    private final Executor syncExecutor;

    public ChunkyTemplateTaskFactory(@NotNull ContainerAdapter containerAdapter,
                                     @NotNull EntityRepository<Template, String> templateRepository,
                                     @NotNull Executor syncExecutor) {
        this.containerAdapter = containerAdapter;
        this.templateRepository = templateRepository;

        RegisteredServiceProvider<Chunky> chunkyService = Bukkit.getServicesManager().getRegistration(Chunky.class);
        Objects.requireNonNull(chunkyService, "Chunky service");

        this.chunky = chunkyService.getProvider();
        Objects.requireNonNull(chunky, "chunky");

        this.syncExecutor = syncExecutor;
    }

    @Override
    public void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config) {
        if (!config.getSideSize().isFullRegions()) {
            Message.TEMPLATE_GENERATE_SIDE_SIZE_NOT_EXACT.send(sender, config.getSideSize());
            return;
        }

        WorldCreator worldCreator = WorldCreator.name(config.getName())
                .environment(config.getEnvironment());
        if (config.getSeed() != 0) {
            worldCreator.seed(config.getSeed());
        }
        World bukkitWorld = worldCreator.createWorld();

        Objects.requireNonNull(bukkitWorld, "bukkitWorld");

        int radiusInBlocks = config.getSideSize().getBlocks();

        Selection selection = Selection.builder(chunky, new BukkitWorld(bukkitWorld))
                .center(0, 0)
                .radiusX(radiusInBlocks)
                .radiusZ(radiusInBlocks)
                .build();

        GenerationTask generationTask = new GenerationTask(chunky, selection);
        chunky.getGenerationTasks().put(config.getName(), generationTask);

        Message.TEMPLATE_GENERATE_START.send(sender, config.getName(), config.getSideSize());
        chunky.getScheduler().runTask(() -> {
            generationTask.run();
            afterWorldGeneration(config);
        });
    }

    @SneakyThrows
    private void afterWorldGeneration(@NotNull TemplateCreateConfig config) {
        List<SpawnLocationFindResult> locations = generateSpawnLocations(config);

        String worldName = config.getName();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Message.TEMPLATE_GENERATE_NO_WORLD.broadcast(worldName);
            return;
        }
        Path dataFolder = WorldReference.of(world).getDataFolder();

        CompletableFuture.runAsync(() -> Bukkit.unloadWorld(worldName, true), syncExecutor).join();

        if (Bukkit.getWorld(worldName) != null) {
            Message.TEMPLATE_GENERATE_CANNOT_UNLOAD.broadcast(worldName);
            return;
        }

        Path outPath = containerAdapter.resolveData(worldName);

        try {
            Files.createDirectories(outPath);

            List<String> subPaths = List.of("entities", "poi", "region");

            for (String subPath : subPaths) {
                Path fromPath = dataFolder.resolve(subPath);
                if (Files.exists(fromPath)) {
                    Files.move(fromPath, outPath.resolve(subPath), StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (Exception e) {
            Message.TEMPLATE_GENERATE_CANNOT_MOVE_DATA.broadcast(worldName, outPath);
            log.error("An error occurred while moving world files", e);
            return;
        }

        Files.walkFileTree(dataFolder, DeletingFileVisitor.INSTANCE);

        Template template = new Template(
                outPath.getFileName().toString(),
                config.getSideSize(),
                outPath,
                locations
        );

        templateRepository.storeEntity(template);

        Message.TEMPLATE_GENERATE_SUCCESS.broadcast(template.getName(), template.getSideSize(), template.getFolder());
        log.debugIfEnabled("End of generating template with config: {}", config);
    }

    @NotNull
    private List<SpawnLocationFindResult> generateSpawnLocations(@NotNull TemplateCreateConfig config) {
        if (config.getEnvironment() != World.Environment.NORMAL) {
            log.debugIfEnabled("Skipping generation of spawn locations due to the {} environment", config.getEnvironment());
            return Collections.emptyList();
        }

        if (config.getSpawnLocationsCount() == 0) {
            log.debugIfEnabled("Skipping the generation of spawn locations due to the number of 0 specified in the config");
            return Collections.emptyList();
        }

        World world = Bukkit.getWorld(config.getName());
        Objects.requireNonNull(world);

        int locationsCount = config.getSpawnLocationsCount();

        List<SpawnLocationFindResult> locations = new ArrayList<>(locationsCount);

        int radiusInRegions = config.getSideSize().getRegions() / 2;

        GameRegion gameRegion = new GameRegion(
                WorldReference.of(world),
                KeyedCoord.of(-radiusInRegions, -radiusInRegions),
                KeyedCoord.of(radiusInRegions, radiusInRegions)
        );
        int huntersCount = config.getHuntersPerLocationCount();
        RandomGenerator randomGenerator = ThreadLocalRandom.current();
        Vector minDistanceFromRunner = new Vector(5, 2.5, 5);
        Vector maxDistanceFromRunner = new Vector(15, 60, 15);
        Distance runnerSpawnRadiusDistance = Distance.ofRegions(radiusInRegions).subtractChunks(8);

        if (runnerSpawnRadiusDistance.getChunks() == 0) {
            throw new IllegalArgumentException("runnerSpawnRadiusDistance is zero");
        }

        log.debugIfEnabled("Started spawn locations generation with config: minDistance: {}, maxDistance: {}, radius: {} chunks, locationsCount: {}, huntersCount: {}",
                minDistanceFromRunner, maxDistanceFromRunner, runnerSpawnRadiusDistance.getChunks(), locationsCount, huntersCount);
        for (int i = 0; i < locationsCount; i++) {
            log.debugIfEnabled("Generating: {}", i + 1);
            SpawnLocationFinder spawnLocationFinder = new LazySpawnLocationFinder(randomGenerator,
                    minDistanceFromRunner, maxDistanceFromRunner,
                    runnerSpawnRadiusDistance
            );

            locations.add(spawnLocationFinder.find(gameRegion, huntersCount));
            log.debugIfEnabled("Finished generation of spawn location {}", i + 1);
        }

        return Collections.unmodifiableList(locations);
    }
}

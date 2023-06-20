package me.supcheg.advancedmanhunt.template.task.impl;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocations;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocationsEntry;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.task.TemplateTaskFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.Selection;
import org.popcraft.chunky.platform.BukkitWorld;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.random.RandomGenerator;

public class ChunkyTemplateTaskFactory implements TemplateTaskFactory {
    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;
    private final Chunky chunky;
    private final Executor syncExecutor;

    public ChunkyTemplateTaskFactory(@NotNull AdvancedManHuntPlugin plugin, @NotNull Executor syncExecutor) {
        this.plugin = plugin;
        this.logger = plugin.getSLF4JLogger().newChild(ChunkyTemplateTaskFactory.class);

        var chunkyService = Bukkit.getServicesManager().getRegistration(Chunky.class);
        Objects.requireNonNull(chunkyService, "Chunky service");

        this.chunky = chunkyService.getProvider();
        Objects.requireNonNull(chunky, "chunky");

        this.syncExecutor = syncExecutor;
    }

    @Override
    public void runCreateTask(@NotNull CommandSender sender, @NotNull TemplateCreateConfig config) {
        if (!config.getSideSize().isFullRegions()) {
            Message.SIDE_SIZE_NOT_EXACT.send(sender, config.getSideSize());
            return;
        }

        WorldCreator worldCreator = WorldCreator.name(config.getWorldName())
                .environment(config.getEnvironment());
        if (config.getSeed() != 0) {
            worldCreator.seed(config.getSeed());
        }
        World bukkitWorld = worldCreator.createWorld();

        if (bukkitWorld == null) {
            sender.sendPlainMessage("Can't create world with config: " + config);
            return;
        }

        int radiusInBlocks = config.getSideSize().getBlocks();

        Selection selection = Selection.builder(chunky, new BukkitWorld(bukkitWorld))
                .center(0, 0)
                .radiusX(radiusInBlocks)
                .radiusZ(radiusInBlocks)
                .build();

        GenerationTask generationTask = new GenerationTask(chunky, selection);
        chunky.getGenerationTasks().put(config.getWorldName(), generationTask);

        chunky.getScheduler().runTask(() -> {
            generationTask.run();
            generateSpawnLocations(config);
            afterWorldGeneration(config);
        });
    }

    @SneakyThrows
    private void afterWorldGeneration(@NotNull TemplateCreateConfig config) {
        String worldName = config.getWorldName();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            Message.NO_WORLD.broadcast(worldName);
            return;
        }
        Path worldFolder = WorldReference.of(world).getFolder();

        CompletableFuture.runAsync(() -> Bukkit.unloadWorld(worldName, true), syncExecutor).join();

        if (Bukkit.getWorld(worldName) != null) {
            Message.CANNOT_UNLOAD.broadcast(worldName);
            return;
        }

        Path outPath = config.getOut();

        try {
            Files.createDirectories(outPath);

            List<String> subPaths = List.of("entities", "poi", "region");

            for (String subPath : subPaths) {
                Files.move(worldFolder.resolve(subPath), outPath.resolve(subPath), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            Message.CANNOT_MOVE_DATA.broadcast(worldName, outPath);
            logger.error("An error occurred while moving world files", e);
            return;
        }

        Template template = new Template(
                outPath.getFileName().toString(),
                config.getSideSize(),
                outPath
        );

        plugin.getTemplateRepository().addTemplate(template);

        Message.SUCCESSFUL_TEMPLATE_CREATE.broadcast(template.getName(), template.getSideSize(), template.getFolder());
    }

    private void generateSpawnLocations(@NotNull TemplateCreateConfig config) {
        if (config.getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        World world = Bukkit.getWorld(config.getWorldName());
        Objects.requireNonNull(world);

        int locationsCount = config.getSpawnLocationsCount();

        List<CachedSpawnLocationsEntry> entries = new ArrayList<>(locationsCount);

        int radiusInRegions = config.getSideSize().getRegions() / 2;

        GameRegion gameRegion = new GameRegion(
                WorldReference.of(world),
                KeyedCoord.of(-radiusInRegions, -radiusInRegions),
                KeyedCoord.of(radiusInRegions, radiusInRegions)
        );
        RandomGenerator randomGenerator = new SecureRandom();
        Vector minDistanceFromRunner = new Vector(5, 2.5, 5);
        Vector maxDistanceFromRunner = new Vector(15, 60, 15);
        Distance runnerSpawnRadiusDistance = Distance.ofRegions(radiusInRegions).removeChunks(32);

        for (int i = 0; i < locationsCount; i++) {
            var spawnLocationFinder = new LazySpawnLocationFinder(randomGenerator,
                    minDistanceFromRunner, maxDistanceFromRunner,
                    runnerSpawnRadiusDistance
            );

            Location[] hunters = spawnLocationFinder.findForHunters(gameRegion, 15);
            Location runner = spawnLocationFinder.findForRunner(gameRegion);
            Location spectators = spawnLocationFinder.findForSpectators(gameRegion);

            entries.add(new CachedSpawnLocationsEntry(runner, hunters, spectators));
        }

        CachedSpawnLocations locations = new CachedSpawnLocations(gameRegion.getWorld().getSeed(), entries);

        plugin.getContainerAdapter().writeWorldString(
                world,
                "spawn_locations.json",
                plugin.getGson().toJson(locations)
        );
    }
}

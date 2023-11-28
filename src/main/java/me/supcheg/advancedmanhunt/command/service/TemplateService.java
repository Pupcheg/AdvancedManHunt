package me.supcheg.advancedmanhunt.command.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.DeletingFileVisitor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import static me.supcheg.advancedmanhunt.command.util.CommandAssertion.assertIsDirectory;
import static me.supcheg.advancedmanhunt.command.util.CommandAssertion.assertIsRegularFile;
import static me.supcheg.advancedmanhunt.command.util.CommandAssertion.requireNonNull;

@CustomLog
public class TemplateService {
    private static final String TEMPLATE_EXPORT_FILE = "template.json";

    private final EntityRepository<Template, String> repository;
    private final WorldGenerator worldGenerator;
    private final Executor syncExecutor;
    private final Path templatesDirectory;
    private final Gson gson;

    public TemplateService(@NotNull EntityRepository<Template, String> repository,
                           @NotNull WorldGenerator worldGenerator,
                           @NotNull Executor syncExecutor,
                           @NotNull ContainerAdapter adapter,
                           @NotNull Gson gson) {
        this.repository = repository;
        this.worldGenerator = worldGenerator;
        this.syncExecutor = syncExecutor;
        this.templatesDirectory = adapter.resolveData("templates");
        this.gson = gson;
    }

    public void generateTemplate(@NotNull TemplateCreateConfig config) {
        if (!config.getRadius().isFullRegions()) {
            MessageText.TEMPLATE_GENERATE_RADIUS_NOT_EXACT.broadcast(config.getRadius());
            return;
        }

        WorldCreator worldCreator = WorldCreator.name(config.getName())
                .environment(config.getEnvironment());
        if (config.getSeed() != 0) {
            worldCreator.seed(config.getSeed());
        }
        World world = worldCreator.createWorld();
        Objects.requireNonNull(world, "world");

        int radiusInBlocks = config.getRadius().getBlocks();

        MessageText.TEMPLATE_GENERATE_START.broadcast(config.getName(), config.getRadius());
        worldGenerator.generate(world, radiusInBlocks, () -> {
            try {
                afterWorldGeneration(config);
            } catch (Exception e) {
                log.error("An error occurred while generating template", e);
            }
        });
    }

    private void afterWorldGeneration(@NotNull TemplateCreateConfig config) throws IOException {
        String worldName = config.getName();

        MessageText.TEMPLATE_GENERATED_WORLD.broadcast(worldName);
        List<SpawnLocationFindResult> locations = generateSpawnLocations(config);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MessageText.TEMPLATE_GENERATE_NO_WORLD.broadcast(worldName);
            return;
        }

        WorldReference worldReference = WorldReference.of(world);

        CompletableFuture.runAsync(() -> Bukkit.unloadWorld(worldName, true), syncExecutor).join();

        if (Bukkit.getWorld(worldName) != null) {
            MessageText.TEMPLATE_GENERATE_CANNOT_UNLOAD.broadcast(worldName);
            return;
        }

        Path outPath = templatesDirectory.resolve(worldName);

        try {
            Files.createDirectories(outPath);

            List<String> subPaths = List.of("entities", "poi", "region");

            for (String subPath : subPaths) {
                Path fromPath = worldReference.getDataFolder().resolve(subPath);
                if (Files.exists(fromPath)) {
                    Files.move(fromPath, outPath.resolve(subPath), StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (Exception e) {
            MessageText.TEMPLATE_GENERATE_CANNOT_MOVE_DATA.broadcast(worldName, outPath);
            log.error("An error occurred while moving world files", e);
            return;
        }

        Files.walkFileTree(worldReference.getFolder(), DeletingFileVisitor.INSTANCE);

        Template template = new Template(
                outPath.getFileName().toString(),
                config.getRadius(),
                outPath,
                locations
        );

        repository.storeEntity(template);
        repository.save();

        MessageText.TEMPLATE_GENERATE_SUCCESS.broadcast(template.getName(), template.getRadius(), template.getFolder());
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
        Objects.requireNonNull(world, "world");

        int locationsCount = config.getSpawnLocationsCount();

        SpawnLocationFindResult[] locations = new SpawnLocationFindResult[locationsCount];

        int radiusInRegions = config.getRadius().getRegions();

        GameRegion gameRegion = new GameRegion(
                WorldReference.of(world),
                KeyedCoord.of(-radiusInRegions),
                KeyedCoord.of(radiusInRegions)
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

            locations[i] = spawnLocationFinder.find(gameRegion, huntersCount);
            log.debugIfEnabled("Finished generation of spawn location {}", i + 1);
        }

        return List.of(locations);
    }

    @SneakyThrows
    public void importTemplate(@NotNull Path path) {
        assertIsDirectory(path);

        Path templateInfoPath = path.resolve(TEMPLATE_EXPORT_FILE);
        assertIsRegularFile(templateInfoPath);

        Template tmp;
        try (BufferedReader reader = Files.newBufferedReader(templateInfoPath)) {
            tmp = gson.fromJson(reader, Template.class);
        }

        Template template = new Template(
                tmp.getName(),
                tmp.getRadius(),
                path,
                tmp.getSpawnLocations()
        );

        repository.storeEntity(template);
        repository.save();
    }

    @NotNull
    @UnmodifiableView
    public Collection<String> getAllKeys() {
        return repository.getKeys();
    }

    @NotNull
    public Template getTemplate(@NotNull String name) throws CommandSyntaxException {
        return requireNonNull(repository.getEntity(name), "Template");
    }

    public void removeTemplate(@NotNull Template template) {
        repository.invalidateEntity(template);
    }

    @NotNull
    @UnmodifiableView
    public Collection<Template> getAllTemplates() {
        return repository.getEntities();
    }

    @SneakyThrows
    public Path exportTemplate(@NotNull Template template) {
        Path exportPath = template.getFolder().resolve(TEMPLATE_EXPORT_FILE);

        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(Files.newOutputStream(exportPath)))) {
            gson.toJson(template, Template.class, writer);
        }

        return exportPath;
    }

}
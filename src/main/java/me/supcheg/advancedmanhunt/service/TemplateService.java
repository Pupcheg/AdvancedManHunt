package me.supcheg.advancedmanhunt.service;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.CustomLog;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.injector.Injector;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.io.DeletingFileVisitor;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateContext;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import me.supcheg.advancedmanhunt.text.MessageText;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import static me.supcheg.advancedmanhunt.command.exception.CommandAssertions.requireNonNull;

@CustomLog
public class TemplateService {
    private final TemplateRepository repository;
    private final TemplateLoader loader;
    private final WorldGenerator worldGenerator;
    private final Path templatesDirectory;

    public TemplateService(@NotNull TemplateRepository repository,
                           @NotNull TemplateLoader loader,
                           @NotNull WorldGenerator worldGenerator,
                           @NotNull ContainerAdapter adapter) {
        this.repository = repository;
        this.loader = loader;
        this.worldGenerator = worldGenerator;
        this.templatesDirectory = adapter.resolveData("templates");
    }

    public void generateTemplate(@NotNull TemplateCreateContext ctx) {
        if (!ctx.getRadius().isFullRegions()) {
            MessageText.TEMPLATE_GENERATE_RADIUS_NOT_EXACT.sendNullableAndConsole(ctx.getReceiver(), ctx.getRadius());
            return;
        }

        WorldCreator worldCreator = WorldCreator.name(ctx.getName())
                .environment(ctx.getEnvironment().getAsBukkit());
        if (ctx.getSeed() != 0) {
            worldCreator.seed(ctx.getSeed());
        }
        World world = worldCreator.createWorld();
        Objects.requireNonNull(world, "world");

        int radiusInBlocks = ctx.getRadius().getBlocks();


        MessageText.TEMPLATE_GENERATE_START.sendNullableAndConsole(ctx.getReceiver(), ctx.getName(), ctx.getRadius());
        worldGenerator.generate(world, radiusInBlocks, () -> {
            try {
                afterWorldGeneration(ctx);
            } catch (Exception e) {
                log.error("An error occurred while generating template", e);
            }
        });
    }

    private void afterWorldGeneration(@NotNull TemplateCreateContext ctx) throws IOException {
        String worldName = ctx.getName();

        MessageText.TEMPLATE_GENERATED_WORLD.sendNullableAndConsole(ctx.getReceiver(), worldName);
        List<SpawnLocationFindResult> locations = generateSpawnLocations(ctx);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            MessageText.TEMPLATE_GENERATE_NO_WORLD.sendNullableAndConsole(ctx.getReceiver(), worldName);
            return;
        }

        WorldReference worldReference = WorldReference.of(world);

        CompletableFuture.runAsync(() -> Bukkit.unloadWorld(worldName, true), BukkitUtil.mainThreadExecutor()).join();

        if (Bukkit.getWorld(worldName) != null) {
            MessageText.TEMPLATE_GENERATE_CANNOT_UNLOAD.sendNullableAndConsole(ctx.getReceiver(), worldName);
            return;
        }

        Path outPath = templatesDirectory.resolve(worldName);

        Files.createDirectories(outPath);

        List<String> subPaths = List.of("entities", "poi", "region");

        for (String subPath : subPaths) {
            Path fromPath = worldReference.getDataFolder().resolve(subPath);
            if (Files.exists(fromPath)) {
                try {
                    Files.move(fromPath, outPath.resolve(subPath), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    MessageText.TEMPLATE_GENERATE_CANNOT_MOVE_DATA.sendNullableAndConsole(ctx.getReceiver(), worldName, outPath);
                    log.error("An error occurred while moving world files", e);
                    return;
                }
            }
        }

        Path poi = outPath.resolve("poi");
        if (Files.exists(poi)) {
            try (Stream<Path> stream = Files.list(poi)) {
                stream.peek(path -> log.debugIfEnabled("Writing self positions to {}", path))
                        .forEach(Injector.getBridge()::writePositionsToRegion);
            }
        }
        Files.walkFileTree(worldReference.getFolder(), DeletingFileVisitor.INSTANCE);

        Template template = new Template(
                outPath.getFileName().toString(),
                ctx.getRadius(),
                outPath,
                locations
        );

        repository.storeEntity(template);
        repository.save();

        MessageText.TEMPLATE_GENERATE_SUCCESS.sendNullableAndConsole(ctx.getReceiver(),
                template.getName(), template.getRadius(), template.getFolder()
        );
        log.debugIfEnabled("End of generating template with ctx: {}", ctx);
    }

    @NotNull
    private List<SpawnLocationFindResult> generateSpawnLocations(@NotNull TemplateCreateContext config) {
        if (config.getEnvironment() != RealEnvironment.OVERWORLD) {
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
                Coord.coordSameXZ(-radiusInRegions),
                Coord.coordSameXZ(radiusInRegions)
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

    @NotNull
    @UnmodifiableView
    public Collection<String> getAllKeys() {
        return repository.getKeys();
    }

    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        return loader.loadTemplate(region, template);
    }

    @NotNull
    public Template getTemplate(@NotNull String key) throws CommandSyntaxException {
        return requireNonNull(repository.getEntity(key), "template with key=" + key);
    }

    public void removeTemplate(@NotNull Template template) {
        repository.invalidateEntity(template);
    }

    @NotNull
    @UnmodifiableView
    public Collection<Template> getAllTemplates() {
        return repository.getEntities();
    }
}

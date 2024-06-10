package me.supcheg.advancedmanhunt.region.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistration;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.text.MessageText;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.util.Keys.advancedmanhuntKey;
import static me.supcheg.advancedmanhunt.util.Keys.asNamespaced;

@Slf4j
public final class ThriftyGameRegionRepository implements GameRegionRepository, Listener, AutoCloseable {
    private static final String WORLD_PREFIX = "amh_rw-";

    private final SetMultimap<RealEnvironment, WorldReference> worldsCache;
    private final SetMultimap<RealEnvironment, GameRegion> regionsCache;
    private final ListMultimap<WorldReference, GameRegion> world2regions;

    private final ChunkGenerator emptyChunkGenerator = new ChunkGenerator() {/* empty */};
    private final EventListenerRegistration listenerRegistration;
    private int lastWorldId;

    @Inject
    public ThriftyGameRegionRepository(@NotNull EventListenerRegistry listenerRegistry) {
        this.lastWorldId = -1;

        this.worldsCache = MultimapBuilder.enumKeys(RealEnvironment.class).hashSetValues().build();
        this.regionsCache = MultimapBuilder.enumKeys(RealEnvironment.class).hashSetValues().build();
        this.world2regions = MultimapBuilder.hashKeys().linkedListValues().build();
        this.listenerRegistration =listenerRegistry.register(this);
        loadExistingWorlds();
    }

    @Override
    public void close() {
        listenerRegistration.unregister();
    }

    public void loadExistingWorlds() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(WORLD_PREFIX)) {
                addWorld(WorldReference.of(world));
            }
        }
        loadFolderWorlds();
    }

    @Nullable
    @Override
    public GameRegion findRegion(@NotNull Location location) {
        WorldReference world = WorldReference.of(location.getWorld());

        if (!world2regions.containsKey(world)) {
            return null;
        }

        List<GameRegion> regions = world2regions.get(world);

        for (GameRegion region : regions) {
            if (region.positionSource().box().includes(location)) {
                return region;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public GameRegion getRegion(@NotNull RealEnvironment environment) {
        Set<GameRegion> regions = regionsCache.get(environment);
        for (GameRegion region : regions) {
            if (!region.isReserved()) {
                return region;
            }
        }

        Set<WorldReference> worlds = worldsCache.get(environment);
        for (WorldReference worldReference : worlds) {
            List<GameRegion> worldRegions = world2regions.get(worldReference);
            if (worldRegions.size() < config().region.maxRegionsPerWorld) {
                GameRegion region = createRegion(worldReference);
                regions.add(region);
                world2regions.put(worldReference, region);
                return region;
            }
        }

        String worldName = WORLD_PREFIX + ++lastWorldId + environment.getPostfix();

        WorldReference world = loadWorld(worldName, environment);
        worlds.add(world);

        GameRegion region = createRegion(world);
        world2regions.put(world, region);

        return region;
    }

    @NotNull
    @Contract("_ -> new")
    private GameRegion createRegion(@NotNull WorldReference worldReference) {
        List<GameRegion> regions = world2regions.get(worldReference);

        int blockX;
        int blockZ;
        if (regions.isEmpty()) {
            blockX = blockZ = 0;
        } else {
            GameRegion lastRegion = regions.get(regions.size() - 1);

            blockX = lastRegion.start().getBlockX() + 5 * Distance.REGIONS;
            blockZ = lastRegion.end().getRegionZ() + 6 * Distance.REGIONS - 1;
        }

        int regionSideSizeInBlocks = MAX_REGION_SIDE_SIZE.getBlocks();

        DistancePair start = DistancePair.ofBlocks(blockX, blockZ);
        DistancePair end = DistancePair.ofBlocks(blockX + regionSideSizeInBlocks, blockZ + regionSideSizeInBlocks);
        GameRegion region = new GameRegion(worldReference, start, end);

        regionsCache.put(worldReference.getEnvironment(), region);

        log.debug("Created new region: {}", region);
        return region;
    }

    private void loadFolderWorlds() {
        for (String worldName : listAllWorldNames()) {
            if (worldName.startsWith(WORLD_PREFIX) && Bukkit.getWorld(worldName) == null) {
                RealEnvironment environment = RealEnvironment.fromWorldName(worldName);
                WorldReference world = loadWorld(worldName, environment);
                addWorld(world);
            }
        }
    }

    @SneakyThrows
    private static List<String> listAllWorldNames() {
        try (Stream<Path> stream = Files.list(Bukkit.getWorldContainer().toPath())) {
            return stream.map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        }
    }

    @NotNull
    private WorldReference loadWorld(@NotNull String worldName, @NotNull RealEnvironment environment) {
        World world = WorldCreator.ofKey(asNamespaced(advancedmanhuntKey(worldName)))
                .generator(emptyChunkGenerator)
                .environment(environment.getAsBukkit())
                .keepSpawnLoaded(TriState.FALSE)
                .createWorld();
        log.debug("Created/Loaded world: {} ({})", worldName, world);
        return WorldReference.of(world);
    }

    private void addWorld(@NotNull WorldReference world) {
        if (worldsCache.containsValue(world)) {
            return;
        }

        worldsCache.put(world.getEnvironment(), world);
        int id = Integer.parseInt(world.getName().substring(WORLD_PREFIX.length()).split("_", 2)[0]);
        lastWorldId = Math.max(lastWorldId, id);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        World world = event.getWorld();
        if (world.getName().startsWith(WORLD_PREFIX)) {
            addWorld(WorldReference.of(world));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldUnload(@NotNull WorldUnloadEvent event) {
        World world = event.getWorld();
        WorldReference worldReference = WorldReference.of(world);
        if (world2regions.containsKey(worldReference)) {
            event.setCancelled(true);

            MessageText.CANCELLED_UNLOAD.broadcast(world.getName());
        }
    }
}

package me.supcheg.advancedmanhunt.region.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
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
public class DefaultGameRegionRepository implements GameRegionRepository, Listener {
    private static final String WORLD_PREFIX = "amh_rw-";

    private final SetMultimap<RealEnvironment, WorldReference> worldsCache;
    private final SetMultimap<RealEnvironment, GameRegion> regionsCache;
    private final ListMultimap<WorldReference, GameRegion> world2regions;

    private final ChunkGenerator emptyChunkGenerator = new ChunkGenerator() {/* empty */};
    private int lastWorldId;

    @Inject
    public DefaultGameRegionRepository() {
        this.lastWorldId = -1;

        this.worldsCache = MultimapBuilder.enumKeys(RealEnvironment.class).hashSetValues().build();
        this.regionsCache = MultimapBuilder.enumKeys(RealEnvironment.class).hashSetValues().build();
        this.world2regions = MultimapBuilder.hashKeys().linkedListValues().build();

        registerEventListener();
        loadExistingWorlds();
    }

    public void registerEventListener() {
        BukkitUtil.registerEventListener(this);
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

        Coord blockCoord = Coord.asKeyedCoord(location);

        for (GameRegion region : regions) {
            if (Coords.isInBoundInclusive(blockCoord, region.getStartBlock(), region.getEndBlock())) {
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

        int regionX;
        int regionZ;
        if (regions.isEmpty()) {
            regionX = regionZ = 0;
        } else {
            GameRegion lastRegion = regions.get(regions.size() - 1);

            regionX = lastRegion.getStartRegion().getX() + 5;
            regionZ = lastRegion.getEndRegion().getZ() + 5;
        }

        int regionSideSizeInRegions = MAX_REGION_SIDE_SIZE.getRegions();

        Coord startRegion = Coord.coord(regionX, regionZ);
        Coord endRegion = Coord.coord(regionX + regionSideSizeInRegions, regionZ + regionSideSizeInRegions);
        GameRegion region = new GameRegion(worldReference, startRegion, endRegion);

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

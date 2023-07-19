package me.supcheg.advancedmanhunt.region.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.gson.Gson;
import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.WorldReference;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_REGIONS_PER_WORLD;

public class DefaultGameRegionRepository implements GameRegionRepository, AutoCloseable {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(DefaultGameRegionRepository.class);
    private static final Type GAME_REGION_LIST_TYPE = Types.type(List.class, GameRegion.class);

    private static final String WORLD_PREFIX = "amh_rw-";
    private static final String DATA_FILE_NAME = "amh_data.json";

    private final ContainerAdapter containerAdapter;
    private final Gson gson;

    private final SetMultimap<Environment, WorldReference> worldsCache;
    private final SetMultimap<Environment, GameRegion> regionsCache;
    private final ListMultimap<WorldReference, GameRegion> world2regions;

    private final ChunkGenerator emptyChunkGenerator = new ChunkGenerator() {
    };
    private int lastWorldId;

    public DefaultGameRegionRepository(@NotNull ContainerAdapter containerAdapter, @NotNull Gson gson,
                                       @NotNull EventListenerRegistry eventListenerRegistry) {
        this.containerAdapter = containerAdapter;
        this.gson = gson;
        this.lastWorldId = -1;

        this.worldsCache = MultimapBuilder.enumKeys(Environment.class).hashSetValues().build();
        this.regionsCache = MultimapBuilder.enumKeys(Environment.class).hashSetValues().build();
        this.world2regions = MultimapBuilder.hashKeys().arrayListValues().build();

        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(WORLD_PREFIX)) {
                loadWorld(world);
            }
        }
        loadFolderWorlds();
        eventListenerRegistry.addListener(this);
    }

    @Nullable
    @Override
    public GameRegion findRegion(@NotNull Location location) {
        WorldReference world = WorldReference.of(location.getWorld());

        if (!world2regions.containsKey(world)) {
            return null;
        }

        List<GameRegion> regions = world2regions.get(world);

        KeyedCoord blockCoord = KeyedCoord.asKeyedCoord(location);

        for (GameRegion region : regions) {
            if (CoordUtil.isInBoundInclusive(blockCoord, region.getStartBlock(), region.getEndBlock())) {
                return region;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public GameRegion getRegion(@NotNull Environment environment) {
        if (environment == Environment.CUSTOM) {
            throw new IllegalArgumentException("Unsupported environment type: " + environment);
        }
        Set<GameRegion> regions = regionsCache.get(environment);
        for (GameRegion region : regions) {
            if (!region.isReserved()) {
                return region;
            }
        }

        Set<WorldReference> worlds = worldsCache.get(environment);
        for (WorldReference worldReference : worlds) {
            List<GameRegion> worldRegions = world2regions.get(worldReference);
            if (worldRegions.size() < MAX_REGIONS_PER_WORLD) {
                GameRegion region = createRegion(worldReference);
                regions.add(region);
                world2regions.put(worldReference, region);
                return region;
            }
        }

        String worldName = WORLD_PREFIX + ++lastWorldId + getSuffix(environment);

        World world = Objects.requireNonNull(createWorld(worldName, environment));
        WorldReference worldReference = WorldReference.of(world);
        worlds.add(worldReference);

        GameRegion region = createRegion(worldReference);
        world2regions.put(worldReference, region);

        return region;
    }

    @NotNull
    private static String getSuffix(@NotNull Environment environment) {
        return switch (environment) {
            case NETHER -> "_nether";
            case THE_END -> "_the_end";
            default -> "";
        };
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

        KeyedCoord startRegion = KeyedCoord.of(regionX, regionZ);
        KeyedCoord endRegion = KeyedCoord.of(regionX + regionSideSizeInRegions, regionZ + regionSideSizeInRegions);
        GameRegion region = new GameRegion(worldReference, startRegion, endRegion);

        regionsCache.put(worldReference.getEnvironment(), region);

        LOGGER.debugIfEnabled("Created new region: {}", region);
        return region;
    }

    private void loadFolderWorlds() {
        List<String> worldNames = containerAdapter.getAllWorldNames();

        for (String worldName : worldNames) {
            if (worldName.startsWith(WORLD_PREFIX) && Bukkit.getWorld(worldName) == null) {
                Environment environment;
                if (worldName.endsWith("nether")) {
                    environment = Environment.NETHER;
                } else if (worldName.endsWith("end")) {
                    environment = Environment.THE_END;
                } else {
                    environment = Environment.NORMAL;
                }

                World world = createWorld(worldName, environment);
                if (world != null) {
                    loadWorld(world);
                }
            }
        }
    }

    @Nullable
    private World createWorld(@NotNull String worldName, @NotNull Environment environment) {
        World world = WorldCreator.name(worldName)
                .generator(emptyChunkGenerator)
                .environment(environment)
                .keepSpawnLoaded(TriState.FALSE)
                .createWorld();
        LOGGER.debugIfEnabled("Created new world: {} ({})", worldName, world);
        return world;
    }

    private void loadWorld(@NotNull World world) {
        WorldReference worldReference = WorldReference.of(world);

        if (worldsCache.containsValue(worldReference)) {
            return;
        }

        worldsCache.put(world.getEnvironment(), worldReference);
        int id = Integer.parseInt(world.getName().substring(WORLD_PREFIX.length()).split("_", 2)[0]);
        lastWorldId = Math.max(lastWorldId, id);


        int regionsCount = 0;
        String data = containerAdapter.readWorldString(world, DATA_FILE_NAME);
        if (data != null) {
            Set<GameRegion> env2regions = this.regionsCache.get(world.getEnvironment());
            List<GameRegion> world2regions = this.world2regions.get(worldReference);

            try {
                List<GameRegion> regions = gson.fromJson(data, GAME_REGION_LIST_TYPE);
                env2regions.addAll(regions);
                world2regions.addAll(regions);

                regionsCount = regions.size();
            } catch (Exception e) {
                LOGGER.error("Can't load regions from: {}", data, e);
            }
        }

        LOGGER.debugIfEnabled("Loaded {} game region{} from {}",
                regionsCount, regionsCount == 1 ? "" : "s", world.getName());
    }

    @Override
    public void close() {
        for (Map.Entry<WorldReference, Collection<GameRegion>> entry : world2regions.asMap().entrySet()) {
            World world = entry.getKey().getWorld();
            Collection<GameRegion> regions = entry.getValue();

            String json = gson.toJson(regions);
            containerAdapter.writeWorldString(world, DATA_FILE_NAME, json);
            LOGGER.debugIfEnabled("Saved {} regions for {}", regions.size(), world.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(@NotNull WorldLoadEvent event) {
        World world = event.getWorld();
        if (world.getName().startsWith(WORLD_PREFIX)) {
            loadWorld(world);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWorldUnload(@NotNull WorldUnloadEvent event) {
        World world = event.getWorld();
        WorldReference worldReference = WorldReference.of(world);
        if (world2regions.containsKey(worldReference)) {
            event.setCancelled(true);

            Message.CANCELLED_UNLOAD.broadcast(world.getName());
        }
    }
}

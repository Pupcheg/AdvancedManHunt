package me.supcheg.advancedmanhunt.region.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.exception.RepositoryOverflowException;
import me.supcheg.advancedmanhunt.json.Types;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.Message;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_REGIONS_PER_WORLD;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_WORLDS_PER_ENVIRONMENT;
import static me.supcheg.advancedmanhunt.region.RegionConstants.DISTANCE_BETWEEN_REGIONS;
import static me.supcheg.advancedmanhunt.region.RegionConstants.REGION_SIDE_SIZE;

public class DefaultGameRegionRepository implements GameRegionRepository {

    private static final Type GAME_REGION_LIST_TYPE = Types.type(List.class, GameRegion.class);

    private static final String WORLD_PREFIX = "amh_rw-";
    private static final String DATA_FILE_NAME = "amh_data.json";

    private final AdvancedManHuntPlugin plugin;
    private final CustomLogger logger;

    private final SetMultimap<Environment, WorldReference> worldsCache;
    private final SetMultimap<Environment, GameRegion> regionsCache;
    private final ListMultimap<WorldReference, GameRegion> world2regions;

    private final ListMultimap<WorldReference, GameRegion> unmodifiableRegions;

    private final ChunkGenerator emptyChunkGenerator = new ChunkGenerator() {
    };
    private int lastWorldId;

    public DefaultGameRegionRepository(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
        plugin.addListener(this);
        this.logger = plugin.getSLF4JLogger().newChild(DefaultGameRegionRepository.class);

        this.lastWorldId = -1;

        this.worldsCache = MultimapBuilder.enumKeys(Environment.class).hashSetValues().build();
        this.regionsCache = MultimapBuilder.enumKeys(Environment.class).hashSetValues().build();
        this.world2regions = MultimapBuilder.hashKeys().arrayListValues().build();

        this.unmodifiableRegions = Multimaps.unmodifiableListMultimap(world2regions);

        for (World world : Bukkit.getWorlds()) {
            if (world.getName().startsWith(WORLD_PREFIX)) {
                loadWorld(world);
            }
        }
        loadFolderWorlds();
    }

    @NotNull
    @UnmodifiableView
    @Override
    public ListMultimap<WorldReference, GameRegion> getRegions() {
        return unmodifiableRegions;
    }

    @Nullable
    @Override
    public GameRegion findRegion(@NotNull Location location) {
        WorldReference world = WorldReference.of(location.getWorld());

        if (!world2regions.containsKey(world)) {
            return null;
        }

        List<GameRegion> regions = world2regions.get(world);

        KeyedCoord chunkCoord = KeyedCoord.of(location.getBlockX(), location.getBlockZ());

        for (GameRegion region : regions) {
            if (CoordUtil.isInBoundInclusive(chunkCoord, region.getStartRegion(), region.getEndRegion())) {
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

        if (worlds.size() < MAX_WORLDS_PER_ENVIRONMENT) {
            String worldName = WORLD_PREFIX + ++lastWorldId + getSuffix(environment);

            World world = Objects.requireNonNull(createWorld(worldName, environment));
            WorldReference worldReference = WorldReference.of(world);
            worlds.add(worldReference);

            GameRegion region = createRegion(worldReference);
            world2regions.put(worldReference, region);

            return region;
        }

        String message = "Max regions: %d in %s. Actual worlds count: %d. With regions counts: %s".formatted(
                MAX_REGIONS_PER_WORLD * MAX_WORLDS_PER_ENVIRONMENT, environment,
                world2regions.keySet().size(),
                Arrays.toString(world2regions.asMap().values().stream().mapToInt(Collection::size).toArray())
        );
        throw new RepositoryOverflowException(message);
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
    private GameRegion createRegion(@NotNull WorldReference worldReference) {
        List<GameRegion> regions = world2regions.get(worldReference);

        int regionX;
        int regionZ;
        if (regions.isEmpty()) {
            regionX = regionZ = 0;
        } else {
            GameRegion lastRegion = regions.get(regions.size() - 1);

            regionX = lastRegion.getStartRegion().getX();
            regionZ = lastRegion.getEndRegion().getZ();

            int distanceInRegions = DISTANCE_BETWEEN_REGIONS.getRegions() + 1;
            regionX += distanceInRegions;
            regionZ += distanceInRegions;
        }

        int regionSideSizeInRegions = REGION_SIDE_SIZE.getRegions();

        KeyedCoord startRegion = KeyedCoord.of(regionX, regionZ);
        KeyedCoord endRegion = KeyedCoord.of(regionX + regionSideSizeInRegions, regionZ + regionSideSizeInRegions);
        GameRegion region = new GameRegion(worldReference, startRegion, endRegion);

        regionsCache.put(worldReference.getEnvironment(), region);

        logger.debugIfEnabled("New region: {}", region);
        return region;
    }

    private void loadFolderWorlds() {
        List<String> worldNames = plugin.getContainerAdapter().getAllWorldNames();

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
        logger.debugIfEnabled("Created new world: {} ({})", worldName, world);
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
        String data = plugin.getContainerAdapter().readString(world, DATA_FILE_NAME);
        if (data != null) {
            Set<GameRegion> env2regions = this.regionsCache.get(world.getEnvironment());
            List<GameRegion> world2regions = this.world2regions.get(worldReference);

            try {
                List<GameRegion> regions = plugin.getGson().fromJson(data, GAME_REGION_LIST_TYPE);
                env2regions.addAll(regions);
                world2regions.addAll(regions);

                regionsCount = regions.size();
            } catch (Exception e) {
                logger.error("Can't load regions from: {}", data, e);
            }
        }

        logger.debugIfEnabled("Loaded {} game region{} from {}",
                regionsCount, regionsCount == 1 ? "" : "s", world.getName());
    }

    @Override
    public void close() {
        for (var entry : world2regions.asMap().entrySet()) {
            World world = entry.getKey().getWorld();
            Collection<GameRegion> regions = entry.getValue();

            String json = plugin.getGson().toJson(regions);
            plugin.getContainerAdapter().writeString(world, DATA_FILE_NAME, json);
            logger.debugIfEnabled("Saved {} regions for {}", regions.size(), world.getName());
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

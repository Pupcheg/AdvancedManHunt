package me.supcheg.advancedmanhunt.region;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Game.Portal.*;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;

@AllArgsConstructor
public class RegionPortalHandler implements Listener, AutoCloseable {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(RegionPortalHandler.class);

    private static final KeyedCoord OVERWORLD_SAFE_PORTAL_ZONE_START =
            KeyedCoord.of(-MAX_REGION_SIDE_SIZE.getBlocks() / 2 - OVERWORLD_SAFE_ZONE.getBlocks());
    private static final KeyedCoord OVERWORLD_SAFE_PORTAL_ZONE_END =
            KeyedCoord.of(MAX_REGION_SIDE_SIZE.getBlocks() / 2 - OVERWORLD_SAFE_ZONE.getBlocks());

    private static final KeyedCoord NETHER_SAFE_PORTAL_ZONE_START =
            KeyedCoord.of(-MAX_REGION_SIDE_SIZE.getBlocks() / 2 - NETHER_SAFE_ZONE.getBlocks());
    private static final KeyedCoord NETHER_SAFE_PORTAL_ZONE_END =
            KeyedCoord.of(MAX_REGION_SIDE_SIZE.getBlocks() / 2 - NETHER_SAFE_ZONE.getBlocks());

    private final GameRegionRepository gameRegionRepository;
    private final GameRegion overworld;
    private final GameRegion nether;
    private final GameRegion end;
    private final Location spawnLocation;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerPortal(@NotNull PlayerPortalEvent event) {
        event.setTo(handleEvent(event.getTo(), event.getFrom(), event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEntityPortal(@NotNull EntityPortalEvent event) {
        event.setTo(handleEvent(event.getTo(), event.getFrom(), event.getEntity()));
    }

    @UnknownNullability
    @Contract(value = "null, _, _ -> null; !null, _, _ -> new", pure = true)
    private Location handleEvent(@Nullable Location originalTo, @NotNull Location from, @NotNull Entity entity) {
        if (originalTo == null) {
            LOGGER.debugIfEnabled("Ignoring EntityPortalEvent, because 'to' location is null");
            return null;
        }

        Location to = getValidToLocation(entity, from, originalTo);
        LOGGER.debugIfEnabled("Translated {} to {}. From: {}",
                locationToShortString(originalTo), locationToShortString(to), locationToShortString(from)
        );
        return to;
    }

    @NotNull
    @Contract("_ , _, _ -> new")
    private Location getValidToLocation(@NotNull Entity entity, @NotNull Location from, @NotNull Location to) {
        World fromWorld = from.getWorld();
        World toWorld = to.getWorld();

        switch (fromWorld.getEnvironment()) {
            case NORMAL -> {
                if (shouldHandle(fromWorld, from, overworld)) {
                    switch (toWorld.getEnvironment()) {
                        case NETHER -> to = nether.addDelta(handleOverworldToNether(overworld.removeDelta(from)));
                        case THE_END -> to = end.addDelta(handleOverworldToEnd());
                        default ->
                                throw new IllegalStateException("Unexpected environment: " + toWorld.getEnvironment());
                    }
                }
            }
            case NETHER -> {
                if (toWorld.getEnvironment() == World.Environment.NORMAL && shouldHandle(fromWorld, from, nether)) {
                    to = overworld.addDelta(handleNetherToOverworld(nether.removeDelta(from)));
                }
            }
            case THE_END -> {
                if (toWorld.getEnvironment() == World.Environment.NORMAL && shouldHandle(fromWorld, from, end)) {
                    to = overworld.addDelta(handleEndToOverworld(entity));
                }
            }
        }
        return to;
    }

    @NotNull
    @Contract(pure = true)
    private String locationToShortString(@Nullable Location location) {
        return location == null ? "null" : location.getX() + ", " + location.getY() + ", " + location.getZ()
                + " (" + location.getWorld().getEnvironment() + ")";
    }

    @Contract(pure = true)
    private boolean shouldHandle(@NotNull World world, @NotNull Location location, @NotNull GameRegion expectedRegion) {
        return expectedRegion.getWorldReference().refersTo(world)
                && expectedRegion.equals(gameRegionRepository.findRegion(location));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleOverworldToNether(@NotNull Location overworldLocation) {
        double netherX = overworldLocation.getX() / NETHER_MULTIPLIER;
        double netherZ = overworldLocation.getZ() / NETHER_MULTIPLIER;

        int netherBlocksX = (int) netherX;
        int netherBlocksZ = (int) netherZ;

        if (!CoordUtil.isInBoundInclusive(KeyedCoord.of(netherBlocksX, netherBlocksZ),
                NETHER_SAFE_PORTAL_ZONE_START, NETHER_SAFE_PORTAL_ZONE_END)) {
            netherX = netherBlocksX < NETHER_SAFE_PORTAL_ZONE_START.getX() ?
                    NETHER_SAFE_PORTAL_ZONE_START.getX() : NETHER_SAFE_PORTAL_ZONE_END.getX();

            netherZ = netherBlocksZ < NETHER_SAFE_PORTAL_ZONE_START.getZ() ?
                    NETHER_SAFE_PORTAL_ZONE_START.getZ() : NETHER_SAFE_PORTAL_ZONE_END.getZ();
            LOGGER.debugIfEnabled("Prevented overworld to nether translating overflow");
        }

        return new Location(
                nether.getWorld(),
                netherX, overworldLocation.getY(), netherZ,
                overworldLocation.getYaw(), overworldLocation.getPitch()
        );
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleNetherToOverworld(@NotNull Location netherLocation) {
        double overworldX = netherLocation.getX() * NETHER_MULTIPLIER;
        double overworldZ = netherLocation.getZ() * NETHER_MULTIPLIER;

        int overworldBlocksX = (int) overworldX;
        int overworldBlocksZ = (int) overworldZ;

        if (!CoordUtil.isInBoundInclusive(KeyedCoord.of(overworldBlocksX, overworldBlocksZ),
                OVERWORLD_SAFE_PORTAL_ZONE_START, OVERWORLD_SAFE_PORTAL_ZONE_END)) {
            overworldX = overworldBlocksX < OVERWORLD_SAFE_PORTAL_ZONE_START.getX() ?
                    OVERWORLD_SAFE_PORTAL_ZONE_START.getX() : OVERWORLD_SAFE_PORTAL_ZONE_END.getX();

            overworldZ = overworldBlocksZ < OVERWORLD_SAFE_PORTAL_ZONE_START.getZ() ?
                    OVERWORLD_SAFE_PORTAL_ZONE_START.getZ() : OVERWORLD_SAFE_PORTAL_ZONE_END.getZ();
            LOGGER.debugIfEnabled("Prevented nether to overworld translating overflow");
        }

        return new Location(
                overworld.getWorld(),
                overworldX, netherLocation.getY(), overworldZ,
                netherLocation.getYaw(), netherLocation.getPitch()
        );
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    private Location handleOverworldToEnd() {
        return new Location(end.getWorld(), 100.5, 49, 0.5);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleEndToOverworld(@NotNull Entity entity) {
        Location bedSpawnLocation;
        return (entity instanceof Player player
                && (bedSpawnLocation = player.getBedSpawnLocation()) != null ?
                bedSpawnLocation : spawnLocation).clone();
    }

    @Override
    public void close() {
        PlayerPortalEvent.getHandlerList().unregister(this);
        EntityPortalEvent.getHandlerList().unregister(this);
    }
}

package me.supcheg.advancedmanhunt.region;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
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

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

@CustomLog
@RequiredArgsConstructor
public class RegionPortalHandler implements Listener, AutoCloseable {
    private static final Coord OVERWORLD_SAFE_PORTAL_ZONE_START =
            Coord.coordSameXZ(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks());
    private static final Coord OVERWORLD_SAFE_PORTAL_ZONE_END =
            Coord.coordSameXZ(MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks());

    private static final Coord NETHER_SAFE_PORTAL_ZONE_START =
            Coord.coordSameXZ(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks());
    private static final Coord NETHER_SAFE_PORTAL_ZONE_END =
            Coord.coordSameXZ(MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks());

    private final GameRegionRepository gameRegionRepository;
    private final GameRegion overworld;
    private final GameRegion nether;
    private final GameRegion end;
    private final ImmutableLocation spawnLocation;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerPortal(@NotNull PlayerPortalEvent event) {
        event.setTo(handleEvent(event.getPlayer(), event.getFrom(), event.getTo()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEntityPortal(@NotNull EntityPortalEvent event) {
        event.setTo(handleEvent(event.getEntity(), event.getFrom(), event.getTo()));
    }

    @Nullable
    @Contract(value = "_, _, null -> null; _, _, !null -> new", pure = true)
    private Location handleEvent(@NotNull Entity entity, @NotNull Location from, @Nullable Location originalDestination) {
        if (originalDestination == null) {
            log.debugIfEnabled("Ignoring PortalEvent, because destination location is null");
            return null;
        }

        return getValidDestination(entity, from, originalDestination);
    }

    @NotNull
    @Contract(pure = true)
    private Location getValidDestination(@NotNull Entity entity, @NotNull Location from,
                                         @NotNull Location originalDestination) {
        World fromWorld = from.getWorld();
        RealEnvironment destinationEnvironment = RealEnvironment.fromWorld(originalDestination.getWorld());

        Location destination = originalDestination;

        switch (RealEnvironment.fromBukkit(fromWorld.getEnvironment())) {
            case OVERWORLD -> {
                if (shouldHandle(fromWorld, from, overworld)) {
                    switch (destinationEnvironment) {
                        case NETHER ->
                                destination = nether.addDelta(handleOverworldToNether(overworld.removeDelta(from)));
                        case THE_END -> destination = end.addDelta(handleOverworldToEnd());
                    }
                }
            }
            case NETHER -> {
                if (destinationEnvironment == RealEnvironment.OVERWORLD && shouldHandle(fromWorld, from, nether)) {
                    destination = overworld.addDelta(handleNetherToOverworld(nether.removeDelta(from)));
                }
            }
            case THE_END -> {
                if (destinationEnvironment == RealEnvironment.OVERWORLD && shouldHandle(fromWorld, from, end)) {
                    destination = handleEndToOverworld(entity);
                }
            }
        }
        return destination;
    }

    @Contract(pure = true)
    private boolean shouldHandle(@NotNull World world, @NotNull Location location, @NotNull GameRegion expectedRegion) {
        return expectedRegion.getWorldReference().refersTo(world)
                && expectedRegion.equals(gameRegionRepository.findRegion(location));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleOverworldToNether(@NotNull Location overworldLocation) {
        Coord destination = Coord.coord(
                (int) (overworldLocation.getX() / config().game.portal.netherMultiplier),
                (int) (overworldLocation.getZ() / config().game.portal.netherMultiplier)
        );
        destination = preventBorderExit(destination, NETHER_SAFE_PORTAL_ZONE_START, NETHER_SAFE_PORTAL_ZONE_END);

        return new Location(
                nether.getWorld(),
                destination.getX(), overworldLocation.getY(), destination.getZ(),
                overworldLocation.getYaw(), overworldLocation.getPitch()
        );
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleNetherToOverworld(@NotNull Location netherLocation) {
        Coord destination = Coord.coord(
                (int) (netherLocation.getX() * config().game.portal.netherMultiplier),
                (int) (netherLocation.getZ() * config().game.portal.netherMultiplier)
        );
        destination = preventBorderExit(destination, OVERWORLD_SAFE_PORTAL_ZONE_START, OVERWORLD_SAFE_PORTAL_ZONE_END);

        return new Location(
                overworld.getWorld(),
                destination.getX(), netherLocation.getY(), destination.getZ(),
                netherLocation.getYaw(), netherLocation.getPitch()
        );
    }

    @NotNull
    @Contract(pure = true)
    private static Coord preventBorderExit(@NotNull Coord coord,
                                           @NotNull Coord safeZoneStart, @NotNull Coord safeZoneEnd) {
        return Coords.isInBoundInclusive(coord, safeZoneStart, safeZoneEnd) ?
                coord :
                Coord.coord(
                        coord.getX() < safeZoneStart.getX() ? safeZoneStart.getX() : safeZoneEnd.getX(),
                        coord.getZ() < safeZoneStart.getZ() ? safeZoneStart.getZ() : safeZoneEnd.getZ()
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
                && (bedSpawnLocation = player.getRespawnLocation()) != null ?
                bedSpawnLocation.clone() : spawnLocation.asMutable());
    }

    @Override
    public void close() {
        PlayerPortalEvent.getHandlerList().unregister(this);
        EntityPortalEvent.getHandlerList().unregister(this);
    }
}

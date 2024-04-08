package me.supcheg.advancedmanhunt.game.handler;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

@CustomLog
public class RegionPortalHandler extends ManHuntGameHandler {
    private static final Coord OVERWORLD_SAFE_PORTAL_ZONE_START =
            Coord.coordSameXZ(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks());
    private static final Coord OVERWORLD_SAFE_PORTAL_ZONE_END =
            Coord.coordSameXZ(MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks());

    private static final Coord NETHER_SAFE_PORTAL_ZONE_START =
            Coord.coordSameXZ(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks());
    private static final Coord NETHER_SAFE_PORTAL_ZONE_END =
            Coord.coordSameXZ(MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks());

    public RegionPortalHandler(@NotNull ManHuntGame game) {
        super(game);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handlePlayerPortal(@NotNull PlayerPortalEvent event) {
        if (shouldHandleAt(event.getFrom())) {
            event.setTo(handleEvent(event.getPlayer(), event.getFrom(), event.getTo()));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEntityPortal(@NotNull EntityPortalEvent event) {
        if (shouldHandleAt(event.getFrom())) {
            event.setTo(handleEvent(event.getEntity(), event.getFrom(), event.getTo()));
        }
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
                switch (destinationEnvironment) {
                    case NETHER ->
                            destination = game.getNether().addDelta(handleOverworldToNether(game.getOverworld().removeDelta(from)));
                    case THE_END -> destination = game.getEnd().addDelta(handleOverworldToEnd());
                }
            }
            case NETHER -> {
                if (destinationEnvironment == RealEnvironment.OVERWORLD) {
                    destination = game.getOverworld().addDelta(handleNetherToOverworld(game.getNether().removeDelta(from)));
                }
            }
            case THE_END -> {
                if (destinationEnvironment == RealEnvironment.OVERWORLD) {
                    destination = handleEndToOverworld(entity);
                }
            }
        }
        return destination;
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
                game.getNether().getWorld(),
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
                game.getOverworld().getWorld(),
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
        return new Location(game.getEnd().getWorld(), 100.5, 49, 0.5);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleEndToOverworld(@NotNull Entity entity) {
        Location bedSpawnLocation;
        return (entity instanceof Player player
                && (bedSpawnLocation = player.getRespawnLocation()) != null ?
                bedSpawnLocation.clone() : game.getSpawnLocation().asMutable());
    }

    @Override
    public void unregister() {
        PlayerPortalEvent.getHandlerList().unregister(this);
        EntityPortalEvent.getHandlerList().unregister(this);
    }
}

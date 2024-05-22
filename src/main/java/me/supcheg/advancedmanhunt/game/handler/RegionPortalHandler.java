package me.supcheg.advancedmanhunt.game.handler;

import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.math.relative.RelativePosition;
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
import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;
import static me.supcheg.advancedmanhunt.math.distance.DistancePair.ofBlocksSame;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

@Slf4j
public class RegionPortalHandler extends ManHuntGameHandler {
    private static final PositionBox OVERWORLD_SAFE_PORTAL_ZONE = PositionBox.box(
            ofBlocksSame(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks()),
            ofBlocksSame(MAX_REGION_RADIUS.getBlocks() - config().game.portal.overworldSafeZone.getBlocks())
    );
    private static final PositionBox NETHER_SAFE_PORTAL_ZONE = PositionBox.box(
            ofBlocksSame(-MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks()),
            ofBlocksSame(MAX_REGION_RADIUS.getBlocks() - config().game.portal.netherSafeZone.getBlocks())
    );

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
            log.debug("Ignoring PortalEvent, because destination location is null");
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
                    case NETHER -> destination = game.getNether().positionSource()
                            .migrate(handleOverworldToNether(game.getOverworld().positionSource().absolute(from)))
                            .bukkitAbsolute();
                    case THE_END -> destination = game.getEnd().positionSource()
                            .relative(100.5, 49, 0.5)
                            .bukkitAbsolute();
                }
            }
            case NETHER -> {
                if (destinationEnvironment == RealEnvironment.OVERWORLD) {
                    destination = game.getOverworld().positionSource()
                            .migrate(handleNetherToOverworld(game.getOverworld().positionSource().absolute(from)))
                            .bukkitAbsolute();
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
    private RelativePosition handleOverworldToNether(@NotNull RelativePosition pos) {
        PositionBuilder builder = pos.builderRelative();

        builder.x(builder.x() / config().game.portal.netherMultiplier)
                .z(builder.z() / config().game.portal.netherMultiplier);
        preventBorderExit(builder, NETHER_SAFE_PORTAL_ZONE);

        return pos.source().absolute(builder);
    }

    @NotNull
    private RelativePosition handleNetherToOverworld(@NotNull RelativePosition pos) {
        PositionBuilder builder = pos.builderRelative();

        builder.x(builder.x() * config().game.portal.netherMultiplier)
                .z(builder.z() * config().game.portal.netherMultiplier);
        preventBorderExit(builder, OVERWORLD_SAFE_PORTAL_ZONE);

        return pos.source().relative(builder);
    }

    @Contract(pure = true)
    private static void preventBorderExit(@NotNull PositionBuilder builder, @NotNull PositionBox box) {
        if (!box.includes(builder)) {
            builder.x(builder.x() < box.getMin().x() ? box.getMin().x() : box.getMax().x());
            builder.z(builder.z() < box.getMin().z() ? box.getMin().z() : box.getMax().z());
        }
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location handleEndToOverworld(@NotNull Entity entity) {
        Location bedSpawnLocation;
        return (entity instanceof Player player
                && (bedSpawnLocation = player.getRespawnLocation()) != null ?
                bedSpawnLocation.clone() : position(game.getSpawnLocation()).bukkitLocation());
    }

    @Override
    public void unregister() {
        PlayerPortalEvent.getHandlerList().unregister(this);
        EntityPortalEvent.getHandlerList().unregister(this);
    }
}

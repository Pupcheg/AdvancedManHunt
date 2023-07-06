package me.supcheg.advancedmanhunt.region;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RegionPortalHandler implements Listener, AutoCloseable {
    private final CustomLogger logger;
    private final AdvancedManHuntPlugin plugin;
    private final GameRegion overWorld;
    private final GameRegion nether;
    private final GameRegion end;

    private final Location endSpawnLocation;

    public RegionPortalHandler(@NotNull AdvancedManHuntPlugin plugin, @NotNull GameRegion overWorld,
                               @NotNull GameRegion nether, @NotNull GameRegion end) {
        this.plugin = Objects.requireNonNull(plugin, "plguin");
        this.overWorld = Objects.requireNonNull(overWorld, "overWorld");
        this.nether = Objects.requireNonNull(nether, "nether");
        this.end = Objects.requireNonNull(end, "end");
        this.logger = plugin.getSLF4JLogger().newChild(RegionPortalHandler.class);

        endSpawnLocation = new Location(end.getWorld(), 100, 60, 100);// TODO: 06.07.2023 check

        plugin.addListener(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPortal(@NotNull PlayerPortalEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        World fromWorld = from.getWorld();
        World toWorld = to.getWorld();

        switch (fromWorld.getEnvironment()) {
            case NORMAL -> {
                if (shouldHandle(fromWorld, from, overWorld)) {
                    switch (toWorld.getEnvironment()) {
                        case NETHER -> to = handleOverWorldToNether(overWorld.removeDelta(from));
                        case THE_END -> to = handleOverWorldToEnd();
                        default ->
                                throw new IllegalStateException("Unexpected environment: " + toWorld.getEnvironment());
                    }
                }
            }
            case NETHER -> {
                if (toWorld.getEnvironment() == World.Environment.NORMAL && shouldHandle(fromWorld, from, nether)) {
                    to = nether.addDelta(handleNetherToOverWorld(nether.removeDelta(from)));
                }
            }
            case THE_END -> {
                if (toWorld.getEnvironment() == World.Environment.NORMAL && shouldHandle(fromWorld, from, end)) {
                    to = handleEndToOverWorld(event.getPlayer());
                }
            }
        }
        if (to == null) {
            event.setCancelled(true);
        } else {
            event.setTo(to);
        }
    }

    private boolean shouldHandle(@NotNull World world, @NotNull Location location, @NotNull GameRegion expectedRegion) {
        return expectedRegion.getWorldReference().refersTo(world)
                && expectedRegion.equals(plugin.getGameRegionRepository().findRegion(location));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Location handleOverWorldToNether(@NotNull Location overWorldLocation) {
        return new Location(
                overWorldLocation.getWorld(),
                overWorldLocation.getX() * 8,
                overWorldLocation.getY(),
                overWorldLocation.getZ() * 8,
                overWorldLocation.getYaw(),
                overWorldLocation.getPitch()
        );
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Location handleNetherToOverWorld(@NotNull Location netherLocation) {
        return new Location(
                netherLocation.getWorld(),
                netherLocation.getX() / 8,
                netherLocation.getY(),
                netherLocation.getZ() / 8,
                netherLocation.getYaw(),
                netherLocation.getPitch()
        );
    }

    @NotNull
    @Contract(pure = true)
    private Location handleOverWorldToEnd() {
        return endSpawnLocation.clone();
    }

    @Nullable
    @Contract(pure = true)
    private Location handleEndToOverWorld(@NotNull Player player) {
        Location bedSpawnLocation = player.getBedSpawnLocation();
        if (bedSpawnLocation != null) {
            return bedSpawnLocation;
        }

        ManHuntPlayerView playerView = plugin.getPlayerViewRepository().get(player);
        ManHuntGame game = playerView.getGame();

        String errorMessage = null;
        Location location = null;
        if (game != null) {
            location = game.getSpawnLocation();
            if (location == null) {
                errorMessage = "{} ({}) in not-initialized game, which doesn't have a spawn location.";
            }
        } else {
            errorMessage = "{} ({}) is not in game, but trying to teleport to the end" +
                    " from overworld in handled GameRegion.";
        }

        if (location == null) {
            if (AdvancedManHuntConfig.Game.PlayerReturner.USE_IF_DONT_KNOW_WHAT_TO_DO) {
                errorMessage += " Teleported with defined in config PlayerReturner. Event is cancelled";
                plugin.getPlayerReturner().returnPlayer(player);
            } else {
                errorMessage += " Teleporting to world's spawn location";
                location = overWorld.getWorld().getSpawnLocation();
            }

            logger.error(errorMessage, player, playerView);
        }

        return location;
    }

    @Override
    public void close() {
        PlayerPortalEvent.getHandlerList().unregister(this);
        // TODO: 06.07.2023 unregister all
    }
}

package me.supcheg.advancedmanhunt.game.handler;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.event.ManHuntGameStopEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.paper.BukkitUtil;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@CustomLog
public class ManHuntGameStopHandler extends ManHuntGameHandler {
    private final PlayerReturner playerReturner;

    public ManHuntGameStopHandler(@NotNull ManHuntGame game, @NotNull PlayerReturner playerReturner) {
        super(game);
        this.playerReturner = playerReturner;
    }

    @EventHandler
    public void handleRunnerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();
        UUID playerUniqueId = player.getUniqueId();

        if (shouldHandleAt(player.getLocation()) && isPlaying()
                && game.hasRole(playerUniqueId, ManHuntRole.RUNNER)) {

            event.setCancelled(true);
            BukkitUtil.executeOnMainThread(() -> stop(ManHuntRole.HUNTER));
        }

    }

    @EventHandler
    public void handleEnderDragonDeath(@NotNull EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof EnderDragon dragon) || dragon.getDragonBattle() == null) {
            return;
        }

        if (shouldHandleAt(entity.getLocation()) && isPlaying()) {
            stop(ManHuntRole.RUNNER);
        }

    }

    @EventHandler
    public void handlePlayerRespawn(@NotNull PlayerRespawnEvent event) {
        if (event.isBedSpawn()) {
            return;
        }

        if (shouldHandleAt(event.getPlayer().getLocation())) {
            event.setRespawnLocation(Objects.requireNonNull(game.getSpawnLocation(), "#getSpawnLocation()").asMutable());
            log.debugIfEnabled("Relocated respawn location for {}", event.getPlayer());
        }

    }

    void stop(@Nullable ManHuntRole winnerRole) {
        log.debugIfEnabled("Stopping game {}. Winner: {}", game.getUniqueId(), winnerRole);

        if (winnerRole == ManHuntRole.SPECTATOR) {
            throw new IllegalArgumentException("Available parameters are %s, %s or null"
                    .formatted(ManHuntRole.RUNNER, ManHuntRole.HUNTER));
        }

        if (game.getState().ordinal() >= GameState.STOP.ordinal()) {
            throw new IllegalStateException("The game has already been stopped or is in the process of clearing");
        }

        game.setState(GameState.STOP);
        new ManHuntGameStopEvent(game).callEvent();

        clear(game);
    }

    void clear(@NotNull ManHuntGame game) {
        if (game.getState().ordinal() >= GameState.CLEAR.ordinal()) {
            throw new IllegalStateException("The game is already in the process of being cleaned up");
        }
        game.setState(GameState.CLEAR);

        game.getHandlers().values().forEach(ManHuntGameHandler::unregister);
        game.getTimers().forEach(CountDownTimer::cancel);
        game.getFreezeGroups().forEach(FreezeGroup::clear);

        Players.forEach(game.getMembers(), playerReturner::returnPlayer);

        game.getOverworld().setReserved(false);
        game.getNether().setReserved(false);
        game.getEnd().setReserved(false);
    }

    @Override
    public void unregister() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);
        PlayerRespawnEvent.getHandlerList().unregister(this);
    }
}

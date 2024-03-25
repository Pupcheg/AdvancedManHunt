package me.supcheg.advancedmanhunt.game;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@RequiredArgsConstructor
public class SafeLeaveHandler implements Listener, AutoCloseable {
    private final ManHuntGame game;
    private CountDownTimer timer;
    private long endTime;

    @EventHandler
    public void handlePlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RealEnvironment environment = RealEnvironment.fromBukkit(player.getWorld().getEnvironment());

        if (timer == null
                || !game.getRegion(environment).contains(player.getLocation())
                || !game.isPlaying()
                || isSpectator(player)) {
            return;
        }

        timer.cancel();
        timer = null;
    }

    @EventHandler
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RealEnvironment environment = RealEnvironment.fromBukkit(player.getWorld().getEnvironment());

        if (!game.getRegion(environment).contains(player.getLocation()) || isSpectator(player)) {
            return;
        }

        if (isSafeLeave()) {
            handleSafeLeave();
        } else {
            handleNotSafeLeave();
        }
    }

    private boolean isSpectator(@NotNull Player player) {
        ManHuntRole role = game.getRole(player.getUniqueId());
        return role == ManHuntRole.SPECTATOR || role == null;
    }

    private boolean isSafeLeave() {
        return game.getState().ordinal() >= GameState.START.ordinal()
                && System.currentTimeMillis() - endTime <= 0
                && Players.isAnyOnline(game.getPlayers());
    }

    private void handleSafeLeave() {
        CountDownTimer existingSafeLeaveTimer = timer;
        if (existingSafeLeaveTimer != null && existingSafeLeaveTimer.isRunning()) {
            return;
        }

        this.timer = CountDownTimer.builder()
                .times((int) config().game.safeLeave.returnDuration.getSeconds())
                .everyPeriod(left -> MessageText.END_IN.sendUniqueIds(game.getMembers(), left))
                .afterComplete(() -> game.stop(null))
                .schedule();
        endTime = game.getStartTime() + config().game.safeLeave.enableAfter.getSeconds() * 1000;
    }

    private void handleNotSafeLeave() {
        game.stop(null);
    }

    @Override
    public void close() {
        PlayerQuitEvent.getHandlerList().unregister(this);
    }
}

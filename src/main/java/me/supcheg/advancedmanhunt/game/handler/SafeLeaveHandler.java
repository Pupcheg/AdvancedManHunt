package me.supcheg.advancedmanhunt.game.handler;

import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.RealEnvironment.environment;

public class SafeLeaveHandler extends ManHuntGameHandler {
    private CountDownTimer timer;
    private long startTime;
    private long endTime;

    public SafeLeaveHandler(@NotNull ManHuntGame game) {
        super(game);
    }

    @EventHandler
    public void handleGameStart(@NotNull ManHuntGameStartEvent event) {
        if (event.getManHuntGame() == game) {
            startTime = System.currentTimeMillis();
        }
    }

    @EventHandler
    public void handlePlayerJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RealEnvironment environment = environment(player.getWorld());

        if (timer == null
                || !game.getRegion(environment).positionSource().box().includes(player.getLocation())
                || !isPlaying()
                || game.hasRole(player.getUniqueId(), ManHuntRole.SPECTATOR)) {
            return;
        }

        timer.cancel();
        timer = null;
    }

    @EventHandler
    public void handlePlayerQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RealEnvironment environment = environment(player.getWorld());

        if (!game.getRegion(environment).positionSource().box().includes(player.getLocation())
                || game.hasRole(player.getUniqueId(), ManHuntRole.SPECTATOR)) {
            return;
        }

        if (isSafeLeave()) {
            handleSafeLeave();
        } else {
            handleNotSafeLeave();
        }
    }

    private boolean isSafeLeave() {
        return game.getState().ordinal() >= GameState.START.ordinal()
                && System.currentTimeMillis() - endTime <= 0
                && !game.members().players().onlinePlayers().isEmpty();
    }

    private void handleSafeLeave() {
        if (timer != null && timer.isRunning()) {
            return;
        }

        this.timer = CountDownTimer.times((int) config().game.safeLeave.returnDuration.getSeconds())
                .everyPeriod(left -> MessageText.END_IN.send(game.members().all(), left))
                .afterComplete(() -> game.getHandler(ManHuntGameStopHandler.class).stop(null))
                .schedule();
        endTime = startTime + config().game.safeLeave.enableAfter.getSeconds() * 1000;
    }

    private void handleNotSafeLeave() {
        game.getHandler(ManHuntGameStopHandler.class).stop(null);
    }
}

package me.supcheg.advancedmanhunt.game.handler;

import me.supcheg.advancedmanhunt.event.ManHuntGameStartEvent;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.text.MessageText;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

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
        RealEnvironment environment = RealEnvironment.fromBukkit(player.getWorld().getEnvironment());

        if (timer == null
                || !game.getRegion(environment).contains(player.getLocation())
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
        RealEnvironment environment = RealEnvironment.fromBukkit(player.getWorld().getEnvironment());

        if (!game.getRegion(environment).contains(player.getLocation())
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
                && Players.isAnyOnline(game.getPlayers());
    }

    private void handleSafeLeave() {
        CountDownTimer existingSafeLeaveTimer = timer;
        if (existingSafeLeaveTimer != null && existingSafeLeaveTimer.isRunning()) {
            return;
        }

        this.timer = CountDownTimer.times((int) config().game.safeLeave.returnDuration.getSeconds())
                .everyPeriod(left -> MessageText.END_IN.sendUniqueIds(game.getMembers(), left))
                .afterComplete(() -> game.getHandler(ManHuntGameStopHandler.class).stop(null))
                .schedule();
        endTime = startTime + config().game.safeLeave.enableAfter.getSeconds() * 1000;
    }

    private void handleNotSafeLeave() {
        game.getHandler(ManHuntGameStopHandler.class).stop(null);
    }

    @Override
    public void unregister() {
        PlayerJoinEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
    }
}

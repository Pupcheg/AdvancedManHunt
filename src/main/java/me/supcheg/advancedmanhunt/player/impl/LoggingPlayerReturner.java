package me.supcheg.advancedmanhunt.player.impl;

import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CustomLog
@RequiredArgsConstructor
public class LoggingPlayerReturner implements PlayerReturner {
    private final PlayerReturner delegate;

    @Override
    public void returnPlayer(@NotNull Player player) {
        log.debugIfEnabled("Accepted {}", player);
        delegate.returnPlayer(player);
    }
}

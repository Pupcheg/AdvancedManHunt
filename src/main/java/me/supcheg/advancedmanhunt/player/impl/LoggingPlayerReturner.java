package me.supcheg.advancedmanhunt.player.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Slf4j
@RequiredArgsConstructor
public class LoggingPlayerReturner implements PlayerReturner {
    private final PlayerReturner delegate;

    @Override
    public void returnPlayer(@NotNull Player player) {
        log.debug("Accepted {}", player);
        delegate.returnPlayer(player);
    }
}

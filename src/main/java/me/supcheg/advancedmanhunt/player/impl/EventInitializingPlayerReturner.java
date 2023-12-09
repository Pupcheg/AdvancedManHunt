package me.supcheg.advancedmanhunt.player.impl;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@CustomLog
public class EventInitializingPlayerReturner implements PlayerReturner {
    private volatile PlayerReturner delegate;

    @Override
    public void returnPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player can't be null");

        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    RegisteredListener[] eventListeners = PlayerReturnerInitializeEvent.getHandlerList().getRegisteredListeners();

                    if (eventListeners.length == 0) {
                        log.error("{} is not handled by any plugin", PlayerReturnerInitializeEvent.class.getSimpleName());
                        return;
                    }

                    PlayerReturnerInitializeEvent event = new PlayerReturnerInitializeEvent(!Bukkit.isPrimaryThread());
                    delegate = event.getPlayerReturner();

                    if (delegate == null) {
                        log.error("{} is handled, but the {} is missing",
                                PlayerReturnerInitializeEvent.class.getSimpleName(), PlayerReturner.class.getSimpleName());
                        return;
                    }
                }
            }
        }
        delegate.returnPlayer(player);
    }
}

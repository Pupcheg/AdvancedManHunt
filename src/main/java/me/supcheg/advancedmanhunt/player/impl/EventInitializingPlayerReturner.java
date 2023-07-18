package me.supcheg.advancedmanhunt.player.impl;

import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventInitializingPlayerReturner implements PlayerReturner {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(EventInitializingPlayerReturner.class);
    private volatile PlayerReturner delegate;


    @Override
    public void returnPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player can't be null");

        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    RegisteredListener[] eventListeners = PlayerReturnerInitializeEvent.getHandlerList().getRegisteredListeners();

                    if (eventListeners.length == 0) {
                        LOGGER.error("PlayerReturnerInitializeEvent is not handled by any plugin");
                        return;
                    }

                    PlayerReturnerInitializeEvent event = new PlayerReturnerInitializeEvent();
                    event.callEvent();
                    delegate = event.getPlayerReturner();

                    if (delegate == null) {
                        LOGGER.error("PlayerReturnerInitializeEvent is handled, but the PlayerReturner is missing");
                        return;
                    }
                }
            }
        }
        delegate.returnPlayer(player);
    }
}

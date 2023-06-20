package me.supcheg.advancedmanhunt.player.impl;

import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EventInitializingPlayerReturner implements PlayerReturner {

    private final CustomLogger logger;
    private volatile PlayerReturner delegate;

    public EventInitializingPlayerReturner(@NotNull AdvancedManHuntPlugin plugin) {
        this.logger = plugin.getSLF4JLogger().newChild(EventInitializingPlayerReturner.class);
    }

    @Override
    public void returnPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player can't be null");

        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    var eventListeners = PlayerReturnerInitializeEvent.getHandlerList().getRegisteredListeners();

                    if (eventListeners.length == 0) {
                        logger.error("PlayerReturnerInitializeEvent is not handled by any plugin");
                        return;
                    }

                    PlayerReturnerInitializeEvent event = new PlayerReturnerInitializeEvent();
                    event.callEvent();
                    delegate = event.getPlayerReturner();

                    if (delegate == null) {
                        logger.error("PlayerReturnerInitializeEvent is handled, but the PlayerReturner is missing");
                        return;
                    }
                }
            }
        }
        delegate.returnPlayer(player);
    }
}

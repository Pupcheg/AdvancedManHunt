package me.supcheg.advancedmanhunt.player.impl;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@CustomLog
public class EventInitializingPlayerReturner implements PlayerReturner {
    private volatile PlayerReturner delegate;
    private boolean eventCalled = false;

    @Override
    public void returnPlayer(@NotNull Player player) {
        Objects.requireNonNull(player, "Player can't be null");

        if (!eventCalled && delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    delegate = callDelegateInitializeEvent();
                    eventCalled = true;
                }
            }
        }

        delegate.returnPlayer(player);
    }

    @NotNull
    private static PlayerReturner callDelegateInitializeEvent() {
        RegisteredListener[] eventListeners = PlayerReturnerInitializeEvent.getHandlerList().getRegisteredListeners();

        if (eventListeners.length == 0) {
            log.error("{} is not handled by any plugin", PlayerReturnerInitializeEvent.class.getSimpleName());
            return __ -> {/* nothing */};
        }

        PlayerReturnerInitializeEvent event = new PlayerReturnerInitializeEvent(!Bukkit.isPrimaryThread());
        event.callEvent();

        if (event.getPlayerReturner() == null) {
            String listenersClassNames = Arrays.stream(eventListeners)
                    .sorted(Comparator.comparing(RegisteredListener::getPriority))
                    .map(RegisteredListener::getListener)
                    .map(Listener::getClass)
                    .map(Class::getName)
                    .collect(Collectors.joining(", ", "[", "]"));

            log.error("{} is handled by {}, but the {} is missing",
                    PlayerReturnerInitializeEvent.class.getSimpleName(),
                    listenersClassNames,
                    PlayerReturner.class.getSimpleName()
            );
            return __ -> {/* nothing */};
        }

        return event.getPlayerReturner();
    }
}

package me.supcheg.advancedmanhunt.event.registry;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface EventListenerRegistry {
    @NotNull
    EventListenerRegistration register(@NotNull Listener listener);
}

package me.supcheg.advancedmanhunt.event;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface EventListenerRegistry {
    void addListener(@NotNull Listener listener);
}

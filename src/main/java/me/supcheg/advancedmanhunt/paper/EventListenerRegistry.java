package me.supcheg.advancedmanhunt.paper;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public interface EventListenerRegistry {
    void addListener(@NotNull Listener listener);
}

package me.supcheg.advancedmanhunt.event.impl;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PluginBasedEventListenerRegistry implements EventListenerRegistry {
    private final Plugin plugin;

    @Override
    public void addListener(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}

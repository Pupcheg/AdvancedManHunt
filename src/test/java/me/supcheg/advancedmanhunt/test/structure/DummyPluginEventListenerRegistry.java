package me.supcheg.advancedmanhunt.test.structure;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.paper.EventListenerRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class DummyPluginEventListenerRegistry implements EventListenerRegistry {

    private final Plugin plugin;

    @Override
    public void addListener(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}

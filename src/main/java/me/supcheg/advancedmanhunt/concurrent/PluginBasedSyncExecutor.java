package me.supcheg.advancedmanhunt.concurrent;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class PluginBasedSyncExecutor implements Executor {
    private final Plugin plugin;

    @Override
    public void execute(@NotNull Runnable command) {
        Bukkit.getScheduler().runTask(plugin, command);
    }
}

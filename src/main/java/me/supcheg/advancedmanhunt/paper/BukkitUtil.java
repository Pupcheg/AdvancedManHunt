package me.supcheg.advancedmanhunt.paper;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Objects;
import java.util.concurrent.Executor;

@SuppressWarnings("UnstableApiUsage")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitUtil {
    @VisibleForTesting
    static JavaPlugin PLUGIN;

    @SneakyThrows
    @NotNull
    public static JavaPlugin getPlugin() {
        if (PLUGIN != null) {
            return PLUGIN;
        }

        ClassLoader classLoader = BukkitUtil.class.getClassLoader();

        if (!(classLoader instanceof ConfiguredPluginClassLoader pluginClassLoader)) {
            throw new IllegalStateException("BukkitUtil class wasn't loaded by plugin, classloader: " + classLoader);
        }

        return Objects.requireNonNull(pluginClassLoader.getPlugin(), "plugin");
    }

    public static void registerEventListener(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    @NotNull
    @Contract(pure = true)
    public static Executor mainThreadExecutor() {
        return BukkitUtil::executeOnMainThread;
    }

    public static void executeOnMainThread(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(getPlugin(), runnable);
    }
}

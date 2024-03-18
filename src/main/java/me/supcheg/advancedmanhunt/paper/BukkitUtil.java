package me.supcheg.advancedmanhunt.paper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;

@SuppressWarnings("UnstableApiUsage")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitUtil {
    @SneakyThrows
    @NotNull
    public static JavaPlugin getPlugin() {
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        Class<?> clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());

        ClassLoader classLoader = clazz.getClassLoader();

        if (!(classLoader instanceof PluginClassLoader pluginClassLoader)) {
            throw new IllegalStateException("Caller class wasn't loaded by plugin");
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

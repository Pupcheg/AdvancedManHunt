package me.supcheg.advancedmanhunt.paper;

import io.papermc.paper.plugin.provider.classloader.ConfiguredPluginClassLoader;
import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.Executor;

@SuppressWarnings("UnstableApiUsage")
@CustomLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitUtil {
    private static final MethodHandle javaPlugin_getFile = unreflectGetFile();
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

    public static boolean isPluginInstalled(@NotNull String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            if (!plugin.isEnabled()) {
                throw new IllegalStateException(name + " is installed, but is not loaded");
            }
            log.debugIfEnabled("Found enabled '{}' plugin", name);
            return true;
        }
        log.debugIfEnabled("Not found '{}' plugin", name);
        return false;
    }

    public static void registerEventListener(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    @SneakyThrows
    @NotNull
    private static MethodHandle unreflectGetFile() {
        Method method = JavaPlugin.class.getDeclaredMethod("getFile");
        method.setAccessible(true);
        return MethodHandles.lookup().unreflect(method);
    }

    @SneakyThrows
    @NotNull
    public static File getFile(@NotNull JavaPlugin plugin) {
        return (File) javaPlugin_getFile.invoke(plugin);
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

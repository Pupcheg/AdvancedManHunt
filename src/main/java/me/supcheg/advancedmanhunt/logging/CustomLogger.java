package me.supcheg.advancedmanhunt.logging;

import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomLogger implements Logger {

    private static final Logger MAIN_PLUGIN_LOGGER = ((PluginClassLoader) CustomLogger.class.getClassLoader()).getPlugin().getSLF4JLogger();

    @Delegate
    private final Logger logger;

    public CustomLogger(@NotNull Logger logger) {
        this.logger = Objects.requireNonNull(logger, "delegate logger");
    }

    public void debugIfEnabled(@NotNull String message, @Nullable Object @NotNull ... objects) {
        if (AdvancedManHuntConfig.ENABLE_DEBUG) {
            info(message, objects);
        }
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static CustomLogger getLogger(@NotNull Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }


    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static CustomLogger getLogger(@NotNull String name) {
        return new CustomLogger(LoggerFactory.getLogger(MAIN_PLUGIN_LOGGER.getName() + '/' + name));
    }

    @Deprecated
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public CustomLogger newChild(@NotNull Class<?> clazz) {
        return newChild(clazz.getSimpleName());
    }

    @Deprecated
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public CustomLogger newChild(@NotNull String name) {
        return new CustomLogger(LoggerFactory.getLogger(logger.getName() + '/' + name));
    }
}

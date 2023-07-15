package me.supcheg.advancedmanhunt.logging;

import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomLogger implements Logger {
    @Delegate
    private final Logger logger;

    private CustomLogger(@NotNull Logger logger) {
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
        return new CustomLogger(LoggerFactory.getLogger(AdvancedManHuntPlugin.NAME + '/' + name));
    }
}

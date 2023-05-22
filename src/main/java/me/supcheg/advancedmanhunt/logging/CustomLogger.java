package me.supcheg.advancedmanhunt.logging;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class CustomLogger implements Logger {
    @Delegate
    private final Logger logger;

    public void debugIfEnabled(@NotNull String message, @Nullable Object @NotNull ... objects) {
        if (AdvancedManHuntConfig.ENABLE_DEBUG) {
            info("[DEBUG] " + message, objects);
        }
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public CustomLogger newChild(@NotNull Class<?> clazz) {
        return newChild(clazz.getSimpleName());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public CustomLogger newChild(@NotNull String name) {
        return new CustomLogger(LoggerFactory.getLogger(name));
    }
}

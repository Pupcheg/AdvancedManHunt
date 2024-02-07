package me.supcheg.advancedmanhunt.log;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomLogger implements Logger {
    @Delegate
    private final Logger logger;

    @SuppressWarnings("unused") // used by lombok.CustomLog annotation
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static CustomLogger getLogger(@NotNull String name) {
        String loggerName = AdvancedManHuntPlugin.NAME + '/' + asSimpleClassName(name);
        return new CustomLogger(LoggerFactory.getLogger(loggerName));
    }

    @NotNull
    @Contract(pure = true)
    private static String asSimpleClassName(@NotNull String possibleClassName) {
        int lastDotIndex = possibleClassName.lastIndexOf('.');
        return lastDotIndex == -1 ? possibleClassName : possibleClassName.substring(lastDotIndex + 1);
    }

    public void debugIfEnabled(@NotNull String message, @Nullable Object @NotNull ... objects) {
        if (AdvancedManHuntConfig.ENABLE_DEBUG) {
            info(message, objects);
        }
    }
}

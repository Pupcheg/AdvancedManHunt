package me.supcheg.advancedmanhunt.logging;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@AllArgsConstructor
public class CustomLogger implements Logger {
    @Delegate
    private final Logger logger;

    public void debugIfEnabled(@NotNull String message, @Nullable Object @NotNull ... objects) {
        if (AdvancedManHuntConfig.ENABLE_DEBUG) {
            info("[DEBUG] " + message, objects);
        }
    }
}

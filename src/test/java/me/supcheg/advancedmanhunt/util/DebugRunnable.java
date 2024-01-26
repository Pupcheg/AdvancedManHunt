package me.supcheg.advancedmanhunt.util;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DebugRunnable implements Runnable {
    private final Runnable delegate;

    public static void execute(@NotNull Runnable runnable) {
        new DebugRunnable(runnable).run();
    }

    @Override
    public void run() {
        boolean oldValue = AdvancedManHuntConfig.ENABLE_DEBUG;
        AdvancedManHuntConfig.ENABLE_DEBUG = true;
        delegate.run();
        AdvancedManHuntConfig.ENABLE_DEBUG = oldValue;
    }
}

package me.supcheg.advancedmanhunt.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@RequiredArgsConstructor
public class DebugRunnable implements Runnable {
    private final Runnable delegate;

    public static void execute(@NotNull Runnable runnable) {
        new DebugRunnable(runnable).run();
    }

    @Override
    public void run() {
        boolean oldValue = config().debug;
        config().debug = true;
        delegate.run();
        config().debug = oldValue;
    }
}

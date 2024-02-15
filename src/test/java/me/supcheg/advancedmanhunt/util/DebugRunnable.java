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
        boolean oldValue = AdvancedManHuntConfig.get().debug;
        AdvancedManHuntConfig.get().debug = true;
        delegate.run();
        AdvancedManHuntConfig.get().debug = oldValue;
    }
}

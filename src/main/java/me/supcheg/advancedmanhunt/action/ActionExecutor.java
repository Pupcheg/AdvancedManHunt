package me.supcheg.advancedmanhunt.action;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

public interface ActionExecutor {
    @NotNull
    @CanIgnoreReturnValue
    RunningAction execute(@NotNull Action action);
}

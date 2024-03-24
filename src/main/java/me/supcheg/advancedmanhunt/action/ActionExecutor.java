package me.supcheg.advancedmanhunt.action;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ActionExecutor {
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<List<ActionThrowable>> execute(@NotNull Action action);
}

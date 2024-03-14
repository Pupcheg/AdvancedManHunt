package me.supcheg.advancedmanhunt.action;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public sealed interface ExecutableActionBuilder permits PlainExecutableAction.Builder {
    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    ExecutableActionBuilder execute(@NotNull ActionRunnable runnable);

    @CanIgnoreReturnValue
    @NotNull
    @Contract("_ -> this")
    ExecutableActionBuilder discard(@NotNull ActionRunnable runnable);

    @NotNull
    @Contract("-> new")
    ExecutableAction build();
}

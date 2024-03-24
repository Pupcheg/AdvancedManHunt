package me.supcheg.advancedmanhunt.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RunningAction {
    default void join() {
        asCompletableFuture().join();
    }

    @NotNull
    CompletableFuture<RunningAction> asCompletableFuture();

    @Unmodifiable
    @NotNull
    @Contract("-> new")
    List<ActionThrowable> listThrowables();

    boolean isInterrupted();

    void interruptWithoutDiscard();

    void interruptAndDiscard();
}

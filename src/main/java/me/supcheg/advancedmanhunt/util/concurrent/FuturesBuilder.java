package me.supcheg.advancedmanhunt.util.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface FuturesBuilder {
    @NotNull
    @Contract("_ -> this")
    FuturesBuilder thenAsync(@NotNull Runnable runnable);

    @NotNull
    @Contract("_ -> this")
    FuturesBuilder thenSync(@NotNull Runnable runnable);

    @NotNull
    CompletableFuture<Void> getCurrentFuture();
}

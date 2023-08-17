package me.supcheg.advancedmanhunt.util.concurrent.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.util.concurrent.FuturesBuilder;
import me.supcheg.advancedmanhunt.util.concurrent.FuturesBuilderFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@AllArgsConstructor
public class DefaultFuturesBuilderFactory implements FuturesBuilderFactory {
    private final Executor syncExecutor;

    @NotNull
    @Override
    public FuturesBuilder create(@NotNull Executor asyncExecutor) {
        return new DefaultFuturesBuilder(asyncExecutor);
    }

    @RequiredArgsConstructor
    private class DefaultFuturesBuilder implements FuturesBuilder {
        private final Executor asyncExecutor;
        private CompletableFuture<Void> lastFuture;

        @NotNull
        @Override
        public FuturesBuilder thenAsync(@NotNull Runnable runnable) {
            return then(runnable, asyncExecutor);
        }

        @NotNull
        @Override
        public FuturesBuilder thenSync(@NotNull Runnable runnable) {
            return then(runnable, syncExecutor);
        }

        @NotNull
        public FuturesBuilder then(@NotNull Runnable runnable, @NotNull Executor executor) {
            if (lastFuture == null) {
                lastFuture = CompletableFuture.runAsync(runnable, executor);
            } else {
                lastFuture = lastFuture.thenRunAsync(runnable, executor);
            }
            return this;
        }

        @NotNull
        @Override
        public CompletableFuture<Void> getCurrentFuture() {
            if (lastFuture == null) {
                throw new IllegalStateException("No CompletableFuture is present");
            }
            return lastFuture;
        }
    }
}

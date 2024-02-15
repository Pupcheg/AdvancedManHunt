package me.supcheg.advancedmanhunt.util.concurrent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompletableFutures {

    @NotNull
    public static <T> CompletableFuture<Void> allOf(@NotNull Collection<T> collection,
                                                    @NotNull Function<T, Runnable> function,
                                                    @NotNull Executor executor) {
        return allOf(collection, t -> CompletableFuture.runAsync(function.apply(t), executor));
    }

    @NotNull
    public static <T> CompletableFuture<Void> allOf(@NotNull Collection<T> collection,
                                                    @NotNull Function<T, CompletableFuture<?>> function) {
        CompletableFuture<?>[] futures = new CompletableFuture[collection.size()];
        int index = 0;
        for (T t : collection) {
            futures[index++] = function.apply(t);
        }

        return CompletableFuture.allOf(futures);
    }
}

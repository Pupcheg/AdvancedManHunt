package me.supcheg.advancedmanhunt.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class CompletableFutures {
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

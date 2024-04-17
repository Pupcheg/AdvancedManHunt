package me.supcheg.advancedmanhunt.concurrent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompletableFutures {
    private static final Collector<CompletableFuture<?>, List<CompletableFuture<?>>, CompletableFuture<?>> JOIN_FUTURES =
            Collector.of(
                    ArrayList::new,
                    List::add,
                    (left, right) -> {
                        left.addAll(right);
                        return left;
                    },
                    futures -> CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
            );

    @NotNull
    public static Collector<CompletableFuture<?>, List<CompletableFuture<?>>, CompletableFuture<?>> joinFutures() {
        return JOIN_FUTURES;
    }
}

package me.supcheg.advancedmanhunt.concurrent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompletableFutures {
    @NotNull
    @Contract("-> new")
    public static Collector<CompletableFuture<?>, List<CompletableFuture<?>>, CompletableFuture<?>> joinFutures() {
        return Collector.of(
                ArrayList::new,
                List::add,
                (left, right) -> { left.addAll(right); return left; },
                futures -> CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
        );
    }
}

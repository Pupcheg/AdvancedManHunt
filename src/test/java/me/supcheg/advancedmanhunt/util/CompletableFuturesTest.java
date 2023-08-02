package me.supcheg.advancedmanhunt.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompletableFuturesTest {

    private ExecutorService executor;

    @BeforeEach
    void setup() {
        executor = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    void shutdown() {
        executor.shutdownNow();
    }

    @Test
    void allOfTest() {
        Runnable runnable = () -> {
            try {
                Thread.sleep(1);
            } catch (Exception ignored) {
            }
        };

        Collection<CompletableFuture<?>> futures = new ArrayList<>();

        CompletableFuture<?> joined = CompletableFutures.allOf(
                Stream.generate(() -> "abc").limit(2000).toList(),
                s -> {
                    CompletableFuture<?> completableFuture = CompletableFuture.runAsync(runnable, executor);
                    futures.add(completableFuture);
                    return completableFuture;
                }
        );
        assertTrue(futures.stream().anyMatch(not(CompletableFuture::isDone)));

        joined.join();

        assertTrue(futures.stream().allMatch(CompletableFuture::isDone));
    }
}

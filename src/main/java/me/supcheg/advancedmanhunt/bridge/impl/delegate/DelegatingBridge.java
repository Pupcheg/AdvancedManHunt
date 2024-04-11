package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import lombok.CustomLog;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@CustomLog
public abstract class DelegatingBridge<T> {
    protected final T delegate;

    @SafeVarargs
    protected DelegatingBridge(@NotNull Supplier<T> @NotNull ... candidates) {
        this.delegate = findDelegate(candidates);
    }

    @NotNull
    private T findDelegate(@NotNull Supplier<T> @NotNull [] candidates) {
        for (Supplier<T> candidate : candidates) {
            try {
                T delegate = candidate.get();
                log.debugIfEnabled("Found valid bridge for this environment: {}", delegate.getClass().getCanonicalName());
                return delegate;
            } catch (Throwable ignored) {
            }
        }
        throw new IllegalStateException(
                "Unable to setup any bridge in this environment for " + getClass().getCanonicalName()
        );
    }
}

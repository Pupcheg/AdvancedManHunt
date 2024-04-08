package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import lombok.CustomLog;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@CustomLog
public abstract class LazyBridge<T> {
    private final Class<T> type;
    private Supplier<T>[] possibles;
    private volatile T delegate;

    @SafeVarargs
    protected LazyBridge(@NotNull Class<T> type, @NotNull Supplier<T> @NotNull ... possibles) {
        this.type = type;
        this.possibles = possibles;
    }

    protected T delegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    initializeDelegate();
                }
            }
        }
        return delegate;
    }

    private void initializeDelegate() {
        if (possibles != null) {
            for (Supplier<T> possible : possibles) {
                try {
                    T t = possible.get();
                    possibles = null;
                    delegate = t;
                    log.debugIfEnabled("Found valid bridge for this environment: {}", t.getClass().getCanonicalName());
                    return;
                } catch (Throwable thr) {
                    log.error("Unable to load bridge for {}, trying next...", type.getCanonicalName(), thr);
                }
            }

            possibles = null;
        }

        throw new IllegalStateException(
                "Unable to setup any bridge in this environment for " + type.getCanonicalName()
        );
    }
}

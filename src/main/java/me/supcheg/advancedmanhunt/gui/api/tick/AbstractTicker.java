package me.supcheg.advancedmanhunt.gui.api.tick;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Positionable;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTicker<T extends AbstractTicker<T, C>, C extends Consumer<?>>
        implements Comparable<T>, Positionable {
    protected final At at;
    protected final Priority priority;
    protected final C consumer;

    @Override
    public int compareTo(@NotNull AbstractTicker o) {
        return priority.compareTo(o.priority);
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static abstract class Builder<T extends AbstractTicker<T, C>, C extends Consumer<?>, B extends Builder<T, C, B>> {
        protected final At at;
        protected Priority priority = Priority.NORMAL;
        protected C consumer;

        @NotNull
        @Contract("_ -> this")
        public B priority(@NotNull Priority priority) {
            Objects.requireNonNull(priority);
            this.priority = priority;
            return uncheckedCast(this);
        }

        @Contract("_ -> this")
        public B consumer(@NotNull C consumer) {
            Objects.requireNonNull(consumer);
            this.consumer = consumer;
            return uncheckedCast(this);
        }

        @NotNull
        @Contract("-> new")
        public abstract T build();
    }
}

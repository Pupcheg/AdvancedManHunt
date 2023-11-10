package me.supcheg.advancedmanhunt.gui.impl.wrapped;

import lombok.Data;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.NotNull;

@Data
public class WrappedButtonTickConsumer implements ButtonTickConsumer, Positionable, Comparable<WrappedButtonTickConsumer> {
    private final At at;
    private final Priority priority;
    @Delegate
    private final ButtonTickConsumer delegate;

    @Override
    public int compareTo(@NotNull WrappedButtonTickConsumer o) {
        return priority.compareTo(o.priority);
    }
}

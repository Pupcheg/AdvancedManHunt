package me.supcheg.advancedmanhunt.gui.impl.wrapped;

import lombok.Data;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.NotNull;

@Data
public class WrappedGuiTickConsumer implements GuiTickConsumer, Positionable, Comparable<WrappedGuiTickConsumer> {
    private final At at;
    private final Priority priority;
    @Delegate
    private final GuiTickConsumer delegate;

    @Override
    public int compareTo(@NotNull WrappedGuiTickConsumer o) {
        return priority.compareTo(o.priority);
    }
}

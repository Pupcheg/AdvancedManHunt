package me.supcheg.advancedmanhunt.gui.impl.wrapped;

import lombok.Data;
import lombok.experimental.Delegate;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.NotNull;

@Data
public class WrappedButtonClickAction implements ButtonClickAction, Comparable<WrappedButtonClickAction> {
    private final Priority priority;
    @Delegate
    private final ButtonClickAction delegate;

    @Override
    public int compareTo(@NotNull WrappedButtonClickAction o) {
        return priority.compareTo(o.priority);
    }
}

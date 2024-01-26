package me.supcheg.advancedmanhunt.gui.api;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public class ButtonClickAction implements Comparable<ButtonClickAction> {
    private final Priority priority;
    private final ButtonClickActionConsumer consumer;

    @NotNull
    @Contract("_ -> new")
    public static Builder action(@NotNull ButtonClickActionConsumer consumer) {
        return new Builder(consumer);
    }

    @Override
    public int compareTo(@NotNull ButtonClickAction o) {
        return priority.compareTo(o.priority);
    }

    public static final class Builder {
        private final ButtonClickActionConsumer consumer;
        private Priority priority = Priority.NORMAL;

        private Builder(@NotNull ButtonClickActionConsumer consumer) {
            this.consumer = consumer;
        }

        @NotNull
        @Contract("_ -> this")
        public Builder priority(@NotNull Priority priority) {
            this.priority = priority;
            return this;
        }

        @NotNull
        @Contract("-> new")
        public ButtonClickAction build() {
            return new ButtonClickAction(priority, consumer);
        }
    }
}

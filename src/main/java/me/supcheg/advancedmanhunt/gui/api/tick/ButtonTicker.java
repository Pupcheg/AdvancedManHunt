package me.supcheg.advancedmanhunt.gui.api.tick;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ButtonTicker extends AbstractTicker<ButtonTicker, ButtonTickConsumer> {

    public ButtonTicker(@NotNull At at, @NotNull Priority priority, @NotNull ButtonTickConsumer consumer) {
        super(at, priority, consumer);
    }

    @NotNull
    @Contract("_ -> new")
    public static Builder at(@NotNull At at) {
        Objects.requireNonNull(at);
        return new Builder(at);
    }

    public static class Builder extends AbstractTicker.Builder<ButtonTicker, ButtonTickConsumer, Builder> {

        private Builder(@NotNull At at) {
            super(at);
        }

        @Override
        @NotNull
        @Contract("-> new")
        public ButtonTicker build() {
            Objects.requireNonNull(consumer);
            return new ButtonTicker(at, priority, consumer);
        }
    }
}

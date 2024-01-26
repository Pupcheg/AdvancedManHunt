package me.supcheg.advancedmanhunt.gui.api.tick;

import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GuiTicker extends AbstractTicker<GuiTicker, GuiTickConsumer> {

    public GuiTicker(@NotNull At at, @NotNull Priority priority, @NotNull GuiTickConsumer consumer) {
        super(at, priority, consumer);
    }

    @NotNull
    @Contract("_ -> new")
    public static Builder at(@NotNull At at) {
        Objects.requireNonNull(at);
        return new Builder(at);
    }

    public static class Builder extends AbstractTicker.Builder<GuiTicker, GuiTickConsumer, Builder> {

        private Builder(@NotNull At at) {
            super(at);
        }

        @Override
        @NotNull
        @Contract("-> new")
        public GuiTicker build() {
            Objects.requireNonNull(consumer);
            return new GuiTicker(at, priority, consumer);
        }
    }
}

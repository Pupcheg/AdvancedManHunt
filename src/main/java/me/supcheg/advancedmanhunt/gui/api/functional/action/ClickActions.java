package me.supcheg.advancedmanhunt.gui.api.functional.action;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class ClickActions {
    private ClickActions() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Contract("_ -> new")
    public static PerformCommandButtonClickActionConsumer performCommand(@NotNull String label) {
        return new PerformCommandButtonClickActionConsumer(label);
    }

    @NotNull
    @Contract("_ -> new")
    public static OpenGuiButtonClickActionConsumer openGui(@NotNull String key) {
        return new OpenGuiButtonClickActionConsumer(key);
    }
}

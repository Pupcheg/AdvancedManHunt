package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ClickActions {
    private ClickActions() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Contract("_ -> new")
    public static ButtonClickActionConsumer performCommand(@NotNull String command) {
        return ctx -> ctx.getPlayer().performCommand(command);
    }

    @NotNull
    @Contract("_ -> new")
    public static ButtonClickActionConsumer performCommand(@NotNull Supplier<String> command) {
        return ctx -> ctx.getPlayer().performCommand(command.get());
    }

    @NotNull
    @Contract("_ -> new")
    public static ButtonClickActionConsumer openGui(@NotNull String key) {
        return ctx -> ctx.getGui().getController().getGuiOrThrow(key).open(ctx.getPlayer());
    }
}

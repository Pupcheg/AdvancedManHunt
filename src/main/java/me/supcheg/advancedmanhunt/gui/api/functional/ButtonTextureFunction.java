package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface ButtonTextureFunction extends Function<ButtonResourceGetContext, String> {
    @NotNull
    String getTexture(ButtonResourceGetContext ctx);

    @NotNull
    @Override
    default String apply(@NotNull ButtonResourceGetContext ctx) {
        return getTexture(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ButtonTextureFunction constant(@NotNull String path) {
        return ctx -> path;
    }
}

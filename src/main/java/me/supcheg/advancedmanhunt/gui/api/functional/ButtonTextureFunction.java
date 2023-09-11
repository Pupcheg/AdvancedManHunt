package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;

import java.util.function.Function;

@FunctionalInterface
public interface ButtonTextureFunction extends Function<ButtonResourceGetContext, String> {
    String getTexture(ButtonResourceGetContext ctx);

    @Override
    default String apply(ButtonResourceGetContext ctx) {
        return getTexture(ctx);
    }

    static ButtonTextureFunction constant(String path) {
        return ctx -> path;
    }
}

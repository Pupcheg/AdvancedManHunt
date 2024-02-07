package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonTextureFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface ButtonTextureFunction extends Function<ButtonResourceGetContext, String> {
    @NotNull
    String getTexture(@NotNull ButtonResourceGetContext ctx);

    /**
     * @deprecated use {@link #getTexture(ButtonResourceGetContext)}
     */
    @Deprecated
    @NotNull
    @Override
    default String apply(@NotNull ButtonResourceGetContext ctx) {
        return getTexture(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ConstantButtonTextureFunction constant(@NotNull String path) {
        return new ConstantButtonTextureFunction(path);
    }

}

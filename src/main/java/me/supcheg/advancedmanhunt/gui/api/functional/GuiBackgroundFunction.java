package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantGuiBackgroundFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface GuiBackgroundFunction extends Function<GuiResourceGetContext, String> {
    @NotNull
    String getBackground(@NotNull GuiResourceGetContext ctx);

    /**
     * @deprecated use {@link #getBackground(GuiResourceGetContext)}
     */
    @Deprecated
    @NotNull
    @Override
    default String apply(@NotNull GuiResourceGetContext ctx) {
        return getBackground(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ConstantGuiBackgroundFunction constant(@NotNull String path) {
        return new ConstantGuiBackgroundFunction(path);
    }

}

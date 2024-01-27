package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
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
    static GuiBackgroundFunction constant(@NotNull String path) {
        return ctx -> path;
    }

    @NotNull
    @Contract("_ -> new")
    static GuiBackgroundFunction delegating(@NotNull MethodHandle handle) {
        return new GuiBackgroundFunction() {
            @SneakyThrows
            @NotNull
            @Override
            public String getBackground(@NotNull GuiResourceGetContext ctx) {
                return (String) Objects.requireNonNull(handle.invoke(ctx), "background");
            }
        };
    }
}

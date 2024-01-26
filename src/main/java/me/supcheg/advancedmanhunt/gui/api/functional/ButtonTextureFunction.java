package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ButtonTextureFunction extends Function<ButtonResourceGetContext, String> {
    @NotNull
    String getTexture(ButtonResourceGetContext ctx);

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
    static ButtonTextureFunction constant(@NotNull String path) {
        return ctx -> path;
    }

    @NotNull
    @Contract("_ -> new")
    static ButtonTextureFunction delegating(@NotNull MethodHandle handle) {
        return new ButtonTextureFunction() {
            @SneakyThrows
            @NotNull
            @Override
            public String getTexture(@NotNull ButtonResourceGetContext ctx) {
                return (String) Objects.requireNonNull(handle.invoke(ctx), "texture");
            }
        };
    }
}

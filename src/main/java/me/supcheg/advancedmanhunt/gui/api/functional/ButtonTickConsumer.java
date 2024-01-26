package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonTickConsumer extends Consumer<ButtonResourceGetContext> {
    @Override
    void accept(@NotNull ButtonResourceGetContext ctx);

    @NotNull
    static ButtonTickConsumer delegating(@NotNull MethodHandle handle) {
        return new ButtonTickConsumer() {
            @SneakyThrows
            @Override
            public void accept(@NotNull ButtonResourceGetContext ctx) {
                handle.invoke(ctx);
            }
        };
    }
}

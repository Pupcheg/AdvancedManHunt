package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonClickActionConsumer extends Consumer<ButtonClickContext> {
    @Override
    void accept(@NotNull ButtonClickContext ctx);

    @NotNull
    static ButtonClickActionConsumer delegating(@NotNull MethodHandle handle) {
        return new ButtonClickActionConsumer() {
            @SneakyThrows
            @Override
            public void accept(@NotNull ButtonClickContext ctx) {
                handle.invoke(ctx);
            }
        };
    }
}

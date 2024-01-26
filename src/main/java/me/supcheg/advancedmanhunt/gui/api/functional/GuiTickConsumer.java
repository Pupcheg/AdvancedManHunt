package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

@FunctionalInterface
public interface GuiTickConsumer extends Consumer<GuiResourceGetContext> {
    @Override
    void accept(@NotNull GuiResourceGetContext ctx);

    @NotNull
    static GuiTickConsumer delegating(@NotNull MethodHandle handle) {
        return new GuiTickConsumer() {
            @SneakyThrows
            @Override
            public void accept(@NotNull GuiResourceGetContext ctx) {
                handle.invoke(ctx);
            }
        };
    }
}

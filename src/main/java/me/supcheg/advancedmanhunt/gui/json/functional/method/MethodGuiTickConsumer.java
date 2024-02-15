package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;


public class MethodGuiTickConsumer extends MethodDelegatingFunctionalInterface implements GuiTickConsumer {

    public MethodGuiTickConsumer(@NotNull String serialized, @NotNull MethodHandle handle) {
        super(serialized, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull GuiTickContext ctx) {
        handle.invoke(getLogicInstance(ctx.getGui()), ctx);
    }
}

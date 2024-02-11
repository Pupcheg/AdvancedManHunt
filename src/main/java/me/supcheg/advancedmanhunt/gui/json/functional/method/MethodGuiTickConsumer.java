package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;


public class MethodGuiTickConsumer extends MethodDelegatingFunctionalInterface implements GuiTickConsumer {

    public MethodGuiTickConsumer(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull GuiTickContext ctx) {
        getHandle().invoke(ctx);
    }
}

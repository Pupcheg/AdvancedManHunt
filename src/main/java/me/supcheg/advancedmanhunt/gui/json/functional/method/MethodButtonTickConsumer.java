package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

public class MethodButtonTickConsumer extends MethodDelegatingFunctionalInterface implements ButtonTickConsumer {

    public MethodButtonTickConsumer(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonTickContext ctx) {
        getHandle().invoke(ctx);
    }
}

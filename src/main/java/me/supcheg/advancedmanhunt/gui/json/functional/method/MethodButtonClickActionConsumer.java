package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

public class MethodButtonClickActionConsumer extends MethodDelegatingFunctionalInterface implements ButtonClickActionConsumer {

    public MethodButtonClickActionConsumer(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        getHandle().invoke(ctx);
    }
}

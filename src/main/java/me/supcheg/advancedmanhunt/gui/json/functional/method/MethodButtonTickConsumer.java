package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

public class MethodButtonTickConsumer extends MethodDelegatingFunctionalInterface implements ButtonTickConsumer {

    public MethodButtonTickConsumer(@NotNull String serialized, @NotNull MethodHandle handle) {
        super(serialized, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonTickContext ctx) {
        accept(ctx.getGui(), ctx);
    }
}

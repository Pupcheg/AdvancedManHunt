package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

public class MethodButtonClickActionConsumer extends MethodDelegatingFunctionalInterface implements ButtonClickActionConsumer {

    public MethodButtonClickActionConsumer(@NotNull String serialized, @NotNull MethodHandle handle) {
        super(serialized, handle);
    }

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        handle.invoke(getLogicInstance(ctx.getGui()), ctx);
    }
}

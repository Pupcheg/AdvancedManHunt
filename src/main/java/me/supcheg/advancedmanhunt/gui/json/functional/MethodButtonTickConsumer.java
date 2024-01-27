package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class MethodButtonTickConsumer implements ButtonTickConsumer {
    private final MethodHandle handle;

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonResourceGetContext ctx) {
        handle.invoke(ctx);
    }
}

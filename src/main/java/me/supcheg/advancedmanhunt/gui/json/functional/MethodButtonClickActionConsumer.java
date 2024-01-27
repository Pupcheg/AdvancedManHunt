package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class MethodButtonClickActionConsumer implements ButtonClickActionConsumer {
    private final MethodHandle handle;

    @SneakyThrows
    @Override
    public void accept(@NotNull ButtonClickContext ctx) {
        handle.invoke(ctx);
    }
}

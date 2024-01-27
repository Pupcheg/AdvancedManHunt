package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiTickConsumer;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

@RequiredArgsConstructor
public class MethodGuiTickConsumer implements GuiTickConsumer {
    private final MethodHandle handle;

    @SneakyThrows
    @Override
    public void accept(@NotNull GuiResourceGetContext ctx) {
        handle.invoke(ctx);
    }
}

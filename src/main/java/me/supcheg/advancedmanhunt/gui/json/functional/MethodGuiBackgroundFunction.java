package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

@RequiredArgsConstructor
public class MethodGuiBackgroundFunction implements GuiBackgroundFunction {
    private final MethodHandle handle;

    @SneakyThrows
    @NotNull
    @Override
    public String getBackground(@NotNull GuiResourceGetContext ctx) {
        return Objects.requireNonNull((String) handle.invoke(ctx), "background");
    }
}

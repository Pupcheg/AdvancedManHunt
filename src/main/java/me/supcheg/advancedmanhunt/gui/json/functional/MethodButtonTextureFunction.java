package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

@RequiredArgsConstructor
public class MethodButtonTextureFunction implements ButtonTextureFunction {
    private final MethodHandle handle;

    @SneakyThrows
    @NotNull
    @Override
    public String getTexture(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull((String) handle.invoke(ctx), "texture");
    }
}

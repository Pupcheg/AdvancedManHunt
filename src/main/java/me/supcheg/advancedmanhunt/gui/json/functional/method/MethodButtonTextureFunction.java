package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.function.Supplier;

public class MethodButtonTextureFunction extends MethodDelegatingFunctionalInterface implements ButtonTextureFunction {

    public MethodButtonTextureFunction(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @NotNull
    @Override
    public String getTexture(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull((String) getHandle().invoke(ctx), "texture");
    }
}

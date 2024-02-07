package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.function.Supplier;

public class MethodGuiBackgroundFunction extends MethodDelegatingFunctionalInterface implements GuiBackgroundFunction {

    public MethodGuiBackgroundFunction(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @NotNull
    @Override
    public String getBackground(@NotNull GuiResourceGetContext ctx) {
        return Objects.requireNonNull((String) getHandle().invoke(ctx), "background");
    }
}

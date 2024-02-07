package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
import java.util.function.Supplier;

public class MethodButtonNameFunction extends MethodDelegatingFunctionalInterface implements ButtonNameFunction {

    public MethodButtonNameFunction(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @NotNull
    @Override
    public Component getName(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull((Component) getHandle().invoke(ctx), "name");
    }
}

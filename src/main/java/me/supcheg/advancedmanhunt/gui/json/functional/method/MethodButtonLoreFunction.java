package me.supcheg.advancedmanhunt.gui.json.functional.method;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.util.Unchecked;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class MethodButtonLoreFunction extends MethodDelegatingFunctionalInterface implements ButtonLoreFunction {

    public MethodButtonLoreFunction(@NotNull String methodName, @NotNull Supplier<MethodHandle> handle) {
        super(methodName, handle);
    }

    @SneakyThrows
    @NotNull
    @Override
    public List<Component> getLore(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull(Unchecked.uncheckedCast(getHandle().invoke(ctx)), "lore");
    }
}

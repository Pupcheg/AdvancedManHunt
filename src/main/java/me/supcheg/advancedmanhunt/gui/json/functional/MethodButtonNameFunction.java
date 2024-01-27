package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

@RequiredArgsConstructor
public class MethodButtonNameFunction implements ButtonNameFunction {
    private final MethodHandle handle;

    @SneakyThrows
    @NotNull
    @Override
    public Component getName(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull((Component) handle.invoke(ctx), "name");
    }
}

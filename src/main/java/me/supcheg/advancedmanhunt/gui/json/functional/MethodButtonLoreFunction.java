package me.supcheg.advancedmanhunt.gui.json.functional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.util.Unchecked;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class MethodButtonLoreFunction implements ButtonLoreFunction {
    private final MethodHandle handle;

    @SneakyThrows
    @NotNull
    @Override
    public List<Component> getLore(@NotNull ButtonResourceGetContext ctx) {
        return Objects.requireNonNull(Unchecked.uncheckedCast(handle.invoke(ctx)), "lore");
    }
}

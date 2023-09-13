package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface ButtonLoreFunction extends Function<ButtonResourceGetContext, List<Component>> {
    @NotNull
    List<Component> getLore(@NotNull ButtonResourceGetContext ctx);

    @NotNull
    @Override
    default List<Component> apply(@NotNull ButtonResourceGetContext ctx) {
        return getLore(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ButtonLoreFunction constant(@NotNull List<Component> lore) {
        return ctx -> lore;
    }
}

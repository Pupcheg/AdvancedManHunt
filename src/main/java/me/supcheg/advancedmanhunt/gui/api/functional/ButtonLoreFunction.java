package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface ButtonLoreFunction extends Function<ButtonResourceGetContext, List<Component>> {
    List<Component> getLore(ButtonResourceGetContext ctx);

    @Override
    default List<Component> apply(ButtonResourceGetContext ctx) {
        return getLore(ctx);
    }

    static ButtonLoreFunction constant(List<Component> lore) {
        return ctx -> lore;
    }
}

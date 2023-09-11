package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import net.kyori.adventure.text.Component;

import java.util.function.Function;

@FunctionalInterface
public interface ButtonNameFunction extends Function<ButtonResourceGetContext, Component> {
    Component getName(ButtonResourceGetContext ctx);

    @Override
    default Component apply(ButtonResourceGetContext ctx) {
        return getName(ctx);
    }

    static ButtonNameFunction constant(Component name) {
        return ctx -> name;
    }
}

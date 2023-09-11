package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;

import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonClickAction extends Consumer<ButtonClickContext> {
    @Override
    void accept(ButtonClickContext ctx);
}

package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonTickConsumer extends Consumer<ButtonResourceGetContext> {
    @Override
    void accept(@NotNull ButtonResourceGetContext ctx);
}

package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonTickConsumer extends Consumer<ButtonTickContext> {
    @Override
    void accept(@NotNull ButtonTickContext ctx);
}

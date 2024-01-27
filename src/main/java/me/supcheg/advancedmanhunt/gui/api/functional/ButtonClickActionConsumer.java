package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface ButtonClickActionConsumer extends Consumer<ButtonClickContext> {
    @Override
    void accept(@NotNull ButtonClickContext ctx);
}

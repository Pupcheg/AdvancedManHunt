package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.GuiTickContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface GuiTickConsumer extends Consumer<GuiTickContext> {
    @Override
    void accept(@NotNull GuiTickContext ctx);
}

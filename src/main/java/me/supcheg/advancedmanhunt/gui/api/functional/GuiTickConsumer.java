package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@FunctionalInterface
public interface GuiTickConsumer extends Consumer<GuiResourceGetContext> {
    @Override
    void accept(@NotNull GuiResourceGetContext ctx);
}

package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.GuiResourceGetContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GuiTickConsumer {
    void accept(@NotNull GuiResourceGetContext ctx);
}

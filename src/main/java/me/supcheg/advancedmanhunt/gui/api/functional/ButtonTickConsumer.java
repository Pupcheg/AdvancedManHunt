package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ButtonTickConsumer {
    void accept(@NotNull ButtonResourceGetContext ctx);
}

package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@FunctionalInterface
public interface ButtonNameFunction extends Function<ButtonResourceGetContext, Component> {
    @NotNull
    Component getName(@NotNull ButtonResourceGetContext ctx);

    default Component getNameWithoutItalic(@NotNull ButtonResourceGetContext ctx) {
        return ComponentUtil.removeItalic(getName(ctx));
    }

    /**
     * @deprecated use {@link #getNameWithoutItalic(ButtonResourceGetContext)}
     */
    @Deprecated
    @NotNull
    @Override
    default Component apply(@NotNull ButtonResourceGetContext ctx) {
        return getNameWithoutItalic(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ButtonNameFunction constant(@NotNull Component name) {
        return ctx -> name;
    }
}

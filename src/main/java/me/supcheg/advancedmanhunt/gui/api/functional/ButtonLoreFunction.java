package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.constant.ConstantButtonLoreFunction;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface ButtonLoreFunction extends Function<ButtonResourceGetContext, List<Component>> {
    @NotNull
    List<Component> getLore(@NotNull ButtonResourceGetContext ctx);

    @NotNull
    default List<Component> getLoreWithoutItalic(@NotNull ButtonResourceGetContext ctx) {
        return ComponentUtil.copyAndRemoveItalic(getLore(ctx));
    }

    /**
     * @deprecated use {@link #getLoreWithoutItalic(ButtonResourceGetContext)}
     */
    @Deprecated
    @NotNull
    @Override
    default List<Component> apply(@NotNull ButtonResourceGetContext ctx) {
        return getLoreWithoutItalic(ctx);
    }

    @NotNull
    @Contract("_ -> new")
    static ConstantButtonLoreFunction constant(@NotNull List<Component> lore) {
        return new ConstantButtonLoreFunction(lore);
    }

}

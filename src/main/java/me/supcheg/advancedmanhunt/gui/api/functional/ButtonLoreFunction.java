package me.supcheg.advancedmanhunt.gui.api.functional;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import me.supcheg.advancedmanhunt.util.Unchecked;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Objects;
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
    static ButtonLoreFunction constant(@NotNull List<Component> lore) {
        return ctx -> lore;
    }

    @NotNull
    @Contract("_ -> new")
    static ButtonLoreFunction delegating(@NotNull MethodHandle handle) {
        return new ButtonLoreFunction() {
            @SneakyThrows
            @NotNull
            @Override
            public List<Component> getLore(@NotNull ButtonResourceGetContext ctx) {
                return Unchecked.uncheckedCast(Objects.requireNonNull(handle.invoke(ctx), "lore"));
            }
        };
    }
}

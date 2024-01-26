package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public interface AdvancedButtonBuilder {

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder slot(int slot);

    @NotNull
    @Contract("_, _, _ -> this")
    AdvancedButtonBuilder slot(int slot1, int slot2, int @NotNull ... otherSlots);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder slot(int @NotNull [] slots);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder slot(@NotNull IntStream slots);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder defaultEnabled(boolean value);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder defaultShown(boolean value);


    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder clickAction(@NotNull ButtonClickAction.Builder action) {
        return clickAction(action.build());
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder clickAction(@NotNull ButtonClickAction action);

    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder texture(@NotNull String subPath) {
        Objects.requireNonNull(subPath, "subPath");
        return texture(ButtonTextureFunction.constant(subPath));
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder texture(@NotNull ButtonTextureFunction function);


    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder name(@NotNull Component name) {
        Objects.requireNonNull(name, "name");
        return name(ButtonNameFunction.constant(name));
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder name(@NotNull ButtonNameFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedButtonBuilder animatedName(@NotNull Duration period, @NotNull ButtonNameFunction function);


    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder lore(@NotNull Component singleLine) {
        Objects.requireNonNull(singleLine, "singleLine");
        return lore(Collections.singletonList(singleLine));
    }

    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder lore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        return lore(ButtonLoreFunction.constant(lore));
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder lore(@NotNull ButtonLoreFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedButtonBuilder animatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function);


    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder ticker(@NotNull ButtonTicker.Builder ticker) {
        Objects.requireNonNull(ticker);
        return ticker(ticker.build());
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder ticker(@NotNull ButtonTicker ticker);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder defaultEnchanted(boolean value);
}

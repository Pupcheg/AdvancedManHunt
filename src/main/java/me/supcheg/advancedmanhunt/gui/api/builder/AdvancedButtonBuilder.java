package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
    @Contract("_, _ -> this")
    AdvancedButtonBuilder clickAction(@NotNull String key, @NotNull ButtonClickAction action);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder texture(@NotNull String subPath);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder texture(@NotNull ButtonTextureFunction function);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder name(@NotNull Component name);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder name(@NotNull ButtonNameFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedButtonBuilder animatedName(@NotNull Duration period, @NotNull ButtonNameFunction function);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder lore(@NotNull List<Component> lore);

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder lore(@NotNull ButtonLoreFunction function);

    @NotNull
    @Contract("_, _ -> this")
    AdvancedButtonBuilder animatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder defaultEnchanted(boolean value);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder renderer(@NotNull ButtonRenderer renderer);
}

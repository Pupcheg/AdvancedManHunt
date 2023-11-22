package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
    @Contract("_ -> this")
    default AdvancedButtonBuilder clickAction(@NotNull ButtonClickAction action) {
        return clickAction(Priority.NORMAL, action);
    }

    @NotNull
    @Contract("_, _ -> this")
    AdvancedButtonBuilder clickAction(@NotNull Priority priority, @NotNull ButtonClickAction action);

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
    default AdvancedButtonBuilder lore(@NotNull Component singleLine) {
        return lore(Collections.singletonList(singleLine));
    }

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
    @Contract("_, _ -> this")
    default AdvancedButtonBuilder tick(@NotNull At at, @NotNull ButtonTickConsumer consumer) {
        return tick(at, Priority.NORMAL, consumer);
    }

    @NotNull
    @Contract("_, _, _ -> this")
    AdvancedButtonBuilder tick(@NotNull At at, @NotNull Priority priority, @NotNull ButtonTickConsumer consumer);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder defaultEnchanted(boolean value);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder renderer(@NotNull ButtonRenderer renderer);
}

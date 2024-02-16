package me.supcheg.advancedmanhunt.gui.api.builder;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@CanIgnoreReturnValue
public sealed interface AdvancedButtonBuilder permits AdvancedButtonBuilderImpl {

    boolean DEFAULT_ENABLED = true;
    boolean DEFAULT_SHOWN = true;
    Component DEFAULT_NAME = Component.empty();
    String DEFAULT_TEXTURE = "button/no_texture_button.png";
    List<Component> DEFAULT_LORE = Collections.emptyList();
    boolean DEFAULT_ENCHANTED = false;

    @NotNull
    @Contract(value = "-> new", pure = true)
    static AdvancedButtonBuilder button() {
        return new AdvancedButtonBuilderImpl();
    }

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
    AdvancedButtonBuilder texture(@NotNull String subPath);


    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder name(@NotNull Component name);


    @NotNull
    @Contract("_ -> this")
    default AdvancedButtonBuilder lore(@NotNull Component singleLine) {
        Objects.requireNonNull(singleLine, "singleLine");
        return lore(Collections.singletonList(singleLine));
    }

    @NotNull
    @Contract("_ -> this")
    AdvancedButtonBuilder lore(@NotNull List<Component> lore);


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

    @NotNull
    IntSet getSlots();

    boolean getDefaultEnabled();

    boolean getDefaultShown();

    @NotNull
    List<ButtonClickAction> getClickActions();

    @NotNull
    String getTexture();

    @NotNull
    Component getName();

    @NotNull
    List<Component> getLore();

    @NotNull
    List<ButtonTicker> getTickers();

    boolean getDefaultEnchanted();
}

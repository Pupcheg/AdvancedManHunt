package me.supcheg.advancedmanhunt.gui.api;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public interface AdvancedButton {

    void enableState(boolean value);

    default void enable() {
        enableState(true);
    }

    boolean isEnabled();


    default void disable() {
        enableState(false);
    }

    default boolean isDisabled() {
        return !isEnabled();
    }


    void showState(boolean value);

    default void show() {
        showState(true);
    }

    boolean isShown();


    default void hide() {
        showState(false);
    }

    default boolean isHidden() {
        return !isShown();
    }


    void addClickAction(@NotNull ButtonClickAction action);

    @NotNull
    Collection<ButtonClickAction> getClickActions();


    void setTexture(@NotNull String path);

    void setName(@NotNull Component name);

    default void setLore(@NotNull Component single) {
        Objects.requireNonNull(single, "single");
        setLore(Collections.singletonList(single));
    }

    default void setLore(@NotNull Component @NotNull ... lore) {
        Objects.requireNonNull(lore, "lore");
        setLore(Arrays.asList(lore));
    }

    void setLore(@NotNull List<Component> lore);


    boolean isEnchanted();

    void setEnchanted(boolean value);
}

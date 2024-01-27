package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
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


    void show();

    boolean isShown();


    void hide();

    boolean isHidden();


    void addClickAction(@NotNull ButtonClickAction action);

    @NotNull
    Collection<ButtonClickAction> getClickActions();


    default void setTexture(@NotNull String resourceJsonPath) {
        Objects.requireNonNull(resourceJsonPath, "resourceJsonPath");
        setTexture(ButtonTextureFunction.constant(resourceJsonPath));
    }

    void setTexture(@NotNull ButtonTextureFunction function);


    default void setName(@NotNull Component name) {
        Objects.requireNonNull(name, "name");
        setName(ButtonNameFunction.constant(name));
    }

    void setName(@NotNull ButtonNameFunction function);


    default void setLore(@NotNull Component @NotNull ... lore) {
        Objects.requireNonNull(lore, "lore");
        setLore(Arrays.asList(lore));
    }

    default void setLore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        setLore(ButtonLoreFunction.constant(lore));
    }

    void setLore(@NotNull ButtonLoreFunction function);


    boolean isEnchanted();

    void setEnchanted(boolean value);

}

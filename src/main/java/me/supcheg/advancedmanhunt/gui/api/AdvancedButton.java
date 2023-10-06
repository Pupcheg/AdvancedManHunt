package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface AdvancedButton {

    void enable();

    void enableFor(@NotNull Duration duration);

    boolean isEnabled();

    @NotNull
    Duration getEnabledDuration();


    void disable();

    void disableFor(@NotNull Duration duration);

    boolean isDisabled();

    @NotNull
    Duration geDisabledDuration();


    void show();

    void showFor(@NotNull Duration duration);

    boolean isShown();

    @NotNull
    Duration getShownDuration();


    void hide();

    void hideFor(@NotNull Duration duration);

    boolean isHidden();

    @NotNull
    Duration getHiddenDuration();


    void addClickAction(@NotNull String key, @NotNull ButtonClickAction action);

    boolean hasClickAction(@NotNull String key);

    boolean removeClickAction(@NotNull String key);

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

    void setAnimatedName(@NotNull Duration period, @NotNull ButtonNameFunction function);


    default void setLore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        setLore(ButtonLoreFunction.constant(lore));
    }

    void setLore(@NotNull ButtonLoreFunction function);

    void setAnimatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function);


    boolean isEnchanted();

    void setEnchanted(boolean value);

    void setEnchantedFor(boolean value, @NotNull Duration duration);

}

package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

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


    void setTexture(@NotNull String resourceJsonPath);

    void lazyTexture(@NotNull ButtonTextureFunction function);


    void setName(@NotNull Component name);

    void lazyName(@NotNull ButtonNameFunction function);

    void animatedName(@NotNull Duration period, @NotNull ButtonNameFunction function);


    void setLore(@NotNull List<Component> lore);

    void lazyLore(@NotNull ButtonLoreFunction function);

    void animatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function);


    boolean isEnchanted();

    void setEnchanted(boolean value);

}

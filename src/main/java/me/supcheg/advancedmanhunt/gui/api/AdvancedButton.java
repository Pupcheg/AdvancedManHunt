package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;

public interface AdvancedButton {

    void enable();

    void enableFor(Duration duration);

    boolean isEnabled();

    Duration getEnabledDuration();


    void disable();

    void disableFor(Duration duration);

    boolean isDisabled();

    Duration geDisabledDuration();


    void show();

    void showFor(Duration duration);

    boolean isShown();

    Duration getShownDuration();


    void hide();

    void hideFor(Duration duration);

    boolean isHidden();

    Duration getHiddenDuration();


    void removeFromAllSlots();


    void addClickAction(String key, ButtonClickAction action);

    boolean hasClickAction(String key);

    boolean removeClickAction(String key);

    Collection<ButtonClickAction> getClickActions();


    void setTexture(String resourceJsonPath);

    void lazyTexture(ButtonTextureFunction function);


    void setName(Component name);

    void lazyName(ButtonNameFunction function);

    void animatedName(Duration period, ButtonNameFunction function);


    void setLore(List<Component> lore);

    void lazyLore(ButtonLoreFunction function);

    void animatedLore(Duration period, ButtonLoreFunction function);


    boolean isEnchanted();

    void setEnchanted(boolean value);

}

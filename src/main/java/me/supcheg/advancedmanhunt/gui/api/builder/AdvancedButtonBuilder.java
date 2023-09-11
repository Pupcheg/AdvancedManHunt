package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.stream.IntStream;

public interface AdvancedButtonBuilder {

    AdvancedButtonBuilder slot(int slot);

    AdvancedButtonBuilder slot(int slot1, int slot2, int... otherSlots);

    AdvancedButtonBuilder slot(int[] slots);

    AdvancedButtonBuilder slot(IntStream slots);


    AdvancedButtonBuilder defaultEnabled(boolean value);

    AdvancedButtonBuilder defaultShows(boolean value);


    AdvancedButtonBuilder clickAction(String key, ButtonClickAction action);

    AdvancedButtonBuilder texture(String subPath);

    AdvancedButtonBuilder lazyTexture(ButtonTextureFunction function);


    AdvancedButtonBuilder name(Component name);

    AdvancedButtonBuilder lazyName(ButtonNameFunction function);

    AdvancedButtonBuilder animatedName(Duration period, ButtonNameFunction function);


    AdvancedButtonBuilder lore(List<Component> lore);

    AdvancedButtonBuilder lazyLore(ButtonLoreFunction function);

    AdvancedButtonBuilder animatedLore(Duration period, ButtonLoreFunction function);


    AdvancedButtonBuilder enchanted(boolean value);


    AdvancedButtonBuilder renderer(ButtonRenderer renderer);
}

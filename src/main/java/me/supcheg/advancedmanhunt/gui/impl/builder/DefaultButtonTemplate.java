package me.supcheg.advancedmanhunt.gui.impl.builder;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.GuiCollections;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DefaultButtonTemplate {
    @Getter
    private final IntSet slots;
    private final List<ButtonClickAction> clickActions;
    private final Map<At, List<ButtonTicker>> tickers;
    private final boolean enabledByDefault;
    private final boolean shownByDefault;

    private final ButtonNameFunction name;
    private final ButtonTextureFunction texture;
    private final ButtonLoreFunction lore;

    private final boolean enchantedByDefault;

    private final ButtonRenderer renderer;

    public DefaultButtonTemplate(@NotNull DefaultAdvancedButtonBuilder builder) {
        slots = builder.slots;
        clickActions = GuiCollections.sortAndTrim(builder.clickActions);
        tickers = GuiCollections.buildSortedConsumersMap(builder.tickers);
        enabledByDefault = builder.enchantedByDefault;
        shownByDefault = builder.shownByDefault;
        name = builder.name;
        texture = builder.texture;
        lore = builder.lore;
        enchantedByDefault = builder.enabledByDefault;
        renderer = builder.renderer;
    }

    @NotNull
    @Contract("_ -> new")
    public DefaultAdvancedButton createButton(@NotNull AdvancedGui gui) {
        return new DefaultAdvancedButton(
                gui,
                new BooleanController(enabledByDefault),
                new BooleanController(shownByDefault),
                new ResourceController<>(texture),
                new ResourceController<>(name),
                new ResourceController<>(lore),
                new BooleanController(enchantedByDefault),
                clickActions,
                tickers,
                renderer
        );
    }
}

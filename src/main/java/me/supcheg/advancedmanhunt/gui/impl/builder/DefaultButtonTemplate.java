package me.supcheg.advancedmanhunt.gui.impl.builder;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.GuiCollections;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonClickAction;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonTickConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DefaultButtonTemplate {
    @Getter
    private final IntSet slots;
    private final List<WrappedButtonClickAction> clickActions;
    private final Map<At, List<WrappedButtonTickConsumer>> tickConsumers;
    private final boolean enabledByDefault;
    private final boolean shownByDefault;

    private final ButtonNameFunction name;
    private final Duration nameChangePeriod;

    private final ButtonTextureFunction texture;

    private final ButtonLoreFunction lore;
    private final Duration loreChangePeriod;

    private final boolean enchantedByDefault;

    private final ButtonRenderer renderer;

    public DefaultButtonTemplate(@NotNull DefaultAdvancedButtonBuilder builder) {
        slots = builder.slots;
        clickActions = GuiCollections.sortAndTrim(builder.clickActions);
        tickConsumers = GuiCollections.buildSortedConsumersMap(builder.tickConsumers);
        enabledByDefault = builder.enchantedByDefault;
        shownByDefault = builder.shownByDefault;
        name = builder.name;
        nameChangePeriod = builder.nameChangePeriod;
        texture = builder.texture;
        lore = builder.lore;
        loreChangePeriod = builder.loreChangePeriod;
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
                new ResourceController<>(texture, Duration.INFINITY),
                new ResourceController<>(name, nameChangePeriod),
                new ResourceController<>(lore, loreChangePeriod),
                new BooleanController(enchantedByDefault),
                clickActions,
                tickConsumers,
                renderer
        );
    }
}

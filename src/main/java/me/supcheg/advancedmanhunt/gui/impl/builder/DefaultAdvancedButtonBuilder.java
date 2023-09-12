package me.supcheg.advancedmanhunt.gui.impl.builder;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.render.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.resource.ButtonResourceController;
import net.kyori.adventure.text.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class DefaultAdvancedButtonBuilder implements AdvancedButtonBuilder {
    private static final ButtonNameFunction DEFAULT_NAME = ButtonNameFunction.constant(Component.empty());
    private static final ButtonTextureFunction DEFAULT_TEXTURE = ButtonTextureFunction.constant("button/no_texture_button.png");
    private static final ButtonLoreFunction DEFAULT_LORE = ButtonLoreFunction.constant(Collections.emptyList());
    private static final ButtonRenderer DEFAULT_BUTTON_RENDERER = ButtonRenderer.standardFromTextureWrapper(new TextureWrapper() {
        @Override
        public int getPaperCustomModelData(String resourcePath) {
            return 0;
        }

        @Override
        public Component getGuiBackgroundComponent(String resourcePath) {
            return Component.text("background");
        }
    });

    @Getter
    private final IntSet slots = new IntOpenHashSet();
    private final Map<String, ButtonClickAction> key2clickAction = new HashMap<>();
    private boolean enabledByDefault = true;
    private boolean shownByDefault = true;

    private ButtonNameFunction name = DEFAULT_NAME;
    private Duration nameChangePeriod = Duration.INFINITY;

    private ButtonTextureFunction texture = DEFAULT_TEXTURE;

    private ButtonLoreFunction lore = DEFAULT_LORE;
    private Duration loreChangePeriod = Duration.INFINITY;

    private boolean enchantedByDefault = false;

    private ButtonRenderer renderer = DEFAULT_BUTTON_RENDERER;

    @Override
    public AdvancedButtonBuilder slot(int slot) {
        slots.add(slot);
        return this;
    }

    @Override
    public AdvancedButtonBuilder slot(int slot1, int slot2, int... otherSlots) {
        slots.add(slot1);
        slots.add(slot2);

        Objects.requireNonNull(otherSlots, "otherSlots");
        for (int slot : otherSlots) {
            this.slots.add(slot);
        }
        return this;
    }

    @Override
    public AdvancedButtonBuilder slot(int[] slots) {
        Objects.requireNonNull(slots, "slots");
        for (int slot : slots) {
            this.slots.add(slot);
        }
        return this;
    }

    @Override
    public AdvancedButtonBuilder slot(IntStream slots) {
        Objects.requireNonNull(slots, "slots");
        slots.forEach(this.slots::add);
        return this;
    }

    @Override
    public AdvancedButtonBuilder defaultEnabled(boolean value) {
        enabledByDefault = value;
        return this;
    }

    @Override
    public AdvancedButtonBuilder defaultShown(boolean value) {
        shownByDefault = value;
        return this;
    }

    @Override
    public AdvancedButtonBuilder clickAction(String key, ButtonClickAction action) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(action, "action");
        key2clickAction.put(key, action);
        return this;
    }

    @Override
    public AdvancedButtonBuilder texture(String subPath) {
        Objects.requireNonNull(subPath, "subPath");
        texture = ButtonTextureFunction.constant(subPath);
        return this;
    }

    @Override
    public AdvancedButtonBuilder lazyTexture(ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        texture = function;
        return this;
    }

    @Override
    public AdvancedButtonBuilder name(Component name) {
        Objects.requireNonNull(name);
        this.name = ButtonNameFunction.constant(name);
        nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @Override
    public AdvancedButtonBuilder lazyName(ButtonNameFunction function) {
        Objects.requireNonNull(function);
        name = function;
        nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @Override
    public AdvancedButtonBuilder animatedName(Duration period, ButtonNameFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        name = function;
        nameChangePeriod = period;
        return this;
    }

    @Override
    public AdvancedButtonBuilder lore(List<Component> lore) {
        Objects.requireNonNull(lore);
        this.lore = ButtonLoreFunction.constant(lore);
        loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @Override
    public AdvancedButtonBuilder lazyLore(ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        lore = function;
        loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @Override
    public AdvancedButtonBuilder animatedLore(Duration period, ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        lore = function;
        loreChangePeriod = period;
        return this;
    }

    @Override
    public AdvancedButtonBuilder enchanted(boolean value) {
        enchantedByDefault = value;
        return this;
    }

    @Override
    public AdvancedButtonBuilder renderer(ButtonRenderer renderer) {
        Objects.requireNonNull(renderer, "renderer");
        this.renderer = renderer;
        return this;
    }

    public DefaultAdvancedButton build(DefaultAdvancedGui gui) {
        return new DefaultAdvancedButton(
                gui,
                new BooleanController(enabledByDefault),
                new BooleanController(shownByDefault),
                new ButtonResourceController<>(texture, Duration.INFINITY),
                new ButtonResourceController<>(name, nameChangePeriod),
                new ButtonResourceController<>(lore, loreChangePeriod),
                new BooleanController(enchantedByDefault),
                key2clickAction,
                renderer
        );
    }
}

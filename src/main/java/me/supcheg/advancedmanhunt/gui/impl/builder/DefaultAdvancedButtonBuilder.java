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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
        public int getPaperCustomModelData(@NotNull String resourcePath) {
            return 0;
        }

        @NotNull
        @Override
        public Component getGuiBackgroundComponent(@NotNull String resourcePath) {
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

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder slot(int slot) {
        slots.add(slot);
        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    @Override
    public AdvancedButtonBuilder slot(int slot1, int slot2, int @NotNull ... otherSlots) {
        slots.add(slot1);
        slots.add(slot2);

        Objects.requireNonNull(otherSlots, "otherSlots");
        for (int slot : otherSlots) {
            this.slots.add(slot);
        }
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder slot(int @NotNull [] slots) {
        Objects.requireNonNull(slots, "slots");
        for (int slot : slots) {
            this.slots.add(slot);
        }
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder slot(@NotNull IntStream slots) {
        Objects.requireNonNull(slots, "slots");
        slots.forEach(this.slots::add);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder defaultEnabled(boolean value) {
        enabledByDefault = value;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder defaultShown(boolean value) {
        shownByDefault = value;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder clickAction(@NotNull String key, @NotNull ButtonClickAction action) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(action, "action");
        key2clickAction.put(key, action);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder texture(@NotNull String subPath) {
        Objects.requireNonNull(subPath, "subPath");
        texture = ButtonTextureFunction.constant(subPath);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lazyTexture(@NotNull ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        texture = function;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder name(@NotNull Component name) {
        Objects.requireNonNull(name);
        this.name = ButtonNameFunction.constant(name);
        nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lazyName(@NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function);
        name = function;
        nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder animatedName(@NotNull Duration period, @NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        name = function;
        nameChangePeriod = period;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore);
        this.lore = ButtonLoreFunction.constant(lore);
        loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lazyLore(@NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        lore = function;
        loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder animatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        lore = function;
        loreChangePeriod = period;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder enchanted(boolean value) {
        enchantedByDefault = value;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder renderer(@NotNull ButtonRenderer renderer) {
        Objects.requireNonNull(renderer, "renderer");
        this.renderer = renderer;
        return this;
    }

    @NotNull
    @Contract("_ -> new")
    public DefaultAdvancedButton build(@NotNull DefaultAdvancedGui gui) {
        int size = gui.getRows() * 9;
        slots.forEach(slot -> {
            if (slot < 0 || slot >= size) {
                throw new IndexOutOfBoundsException(slot);
            }
        });

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

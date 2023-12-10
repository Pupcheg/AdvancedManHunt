package me.supcheg.advancedmanhunt.gui.impl.builder;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTickConsumer;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonClickAction;
import me.supcheg.advancedmanhunt.gui.impl.wrapped.WrappedButtonTickConsumer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DefaultAdvancedButtonBuilder implements AdvancedButtonBuilder {
    private static final boolean DEFAULT_ENABLED = true;
    private static final boolean DEFAULT_SHOWN = true;
    private static final ButtonNameFunction DEFAULT_NAME = ButtonNameFunction.constant(Component.empty());
    private static final ButtonTextureFunction DEFAULT_TEXTURE = ButtonTextureFunction.constant("button/no_texture_button.png");
    private static final ButtonLoreFunction DEFAULT_LORE = ButtonLoreFunction.constant(Collections.emptyList());
    private static final boolean DEFAULT_ENCHANTED = false;

    final IntSet slots;
    final List<WrappedButtonClickAction> clickActions;
    final List<WrappedButtonTickConsumer> tickConsumers;
    boolean enabledByDefault;
    boolean shownByDefault;

    ButtonNameFunction name;
    Duration nameChangePeriod;

    ButtonTextureFunction texture;

    ButtonLoreFunction lore;
    Duration loreChangePeriod;

    boolean enchantedByDefault;

    ButtonRenderer renderer;

    public DefaultAdvancedButtonBuilder(@NotNull ButtonRenderer renderer) {
        this.slots = new IntArraySet();
        this.clickActions = new ArrayList<>();
        this.tickConsumers = new ArrayList<>();

        this.enabledByDefault = DEFAULT_ENABLED;
        this.shownByDefault = DEFAULT_SHOWN;

        this.name = DEFAULT_NAME;
        this.nameChangePeriod = Duration.INFINITY;

        this.texture = DEFAULT_TEXTURE;

        this.lore = DEFAULT_LORE;
        this.loreChangePeriod = Duration.INFINITY;

        this.enchantedByDefault = DEFAULT_ENCHANTED;

        this.renderer = renderer;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder slot(int slot) {
        this.slots.add(slot);
        return this;
    }

    @NotNull
    @Contract("_, _, _ -> this")
    @Override
    public AdvancedButtonBuilder slot(int slot1, int slot2, int @NotNull ... otherSlots) {
        this.slots.add(slot1);
        this.slots.add(slot2);

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
        this.enabledByDefault = value;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder defaultShown(boolean value) {
        this.shownByDefault = value;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder clickAction(@NotNull Priority priority, @NotNull ButtonClickAction action) {
        Objects.requireNonNull(action, "action");
        this.clickActions.add(new WrappedButtonClickAction(priority, action));
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder texture(@NotNull String subPath) {
        Objects.requireNonNull(subPath, "subPath");
        this.texture = ButtonTextureFunction.constant(subPath);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder texture(@NotNull ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        this.texture = function;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder name(@NotNull Component name) {
        Objects.requireNonNull(name);
        this.name = ButtonNameFunction.constant(name);
        this.nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder name(@NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function);
        this.name = function;
        this.nameChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder animatedName(@NotNull Duration period, @NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        this.name = function;
        this.nameChangePeriod = period;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore);
        this.lore = ButtonLoreFunction.constant(lore);
        this.loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lore(@NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        this.lore = function;
        this.loreChangePeriod = Duration.INFINITY;
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    @Override
    public AdvancedButtonBuilder animatedLore(@NotNull Duration period, @NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(period);
        this.lore = function;
        this.loreChangePeriod = period;
        return this;
    }

    @NotNull
    @Override
    public AdvancedButtonBuilder tick(@NotNull At at, @NotNull Priority priority, @NotNull ButtonTickConsumer consumer) {
        this.tickConsumers.add(new WrappedButtonTickConsumer(at, priority, consumer));
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder defaultEnchanted(boolean value) {
        this.enchantedByDefault = value;
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
    @Contract("-> new")
    public DefaultButtonTemplate asTemplate() {
        return new DefaultButtonTemplate(this);
    }
}

package me.supcheg.advancedmanhunt.gui.impl.builder;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
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
    private static final String DEFAULT_TEXTURE = "button/no_texture_button.png";
    private static final Component DEFAULT_NAME = Component.empty();
    private static final List<Component> DEFAULT_LORE = Collections.emptyList();
    private static final boolean DEFAULT_ENCHANTED = false;

    final IntSet slots;
    final List<ButtonClickAction> clickActions;
    final List<ButtonTicker> tickers;
    boolean enabledByDefault;
    boolean shownByDefault;

    String texture;
    Component name;
    List<Component> lore;

    boolean enchantedByDefault;

    final ButtonRenderer renderer;

    public DefaultAdvancedButtonBuilder(@NotNull ButtonRenderer renderer) {
        this.slots = new IntArraySet();
        this.clickActions = new ArrayList<>();
        this.tickers = new ArrayList<>();

        this.enabledByDefault = DEFAULT_ENABLED;
        this.shownByDefault = DEFAULT_SHOWN;

        this.name = DEFAULT_NAME;

        this.texture = DEFAULT_TEXTURE;

        this.lore = DEFAULT_LORE;

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
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder clickAction(@NotNull ButtonClickAction action) {
        Objects.requireNonNull(action, "action");
        this.clickActions.add(action);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder texture(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        this.texture = path;
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder name(@NotNull Component name) {
        Objects.requireNonNull(name, "name");
        this.name = ComponentUtil.removeItalic(name);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    @Override
    public AdvancedButtonBuilder lore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        this.lore = ComponentUtil.copyAndRemoveItalic(lore);
        return this;
    }

    @NotNull
    @Override
    public AdvancedButtonBuilder ticker(@NotNull ButtonTicker ticker) {
        Objects.requireNonNull(ticker);
        tickers.add(ticker);
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
    @Contract("-> new")
    public DefaultButtonTemplate asTemplate() {
        return new DefaultButtonTemplate(this);
    }


    @NotNull
    @Override
    public IntSet getSlots() {
        return slots;
    }

    @Override
    public boolean getDefaultEnabled() {
        return enabledByDefault;
    }

    @Override
    public boolean getDefaultShown() {
        return shownByDefault;
    }

    @NotNull
    @Override
    public List<ButtonClickAction> getClickActions() {
        return clickActions;
    }

    @NotNull
    @Override
    public String getTexture() {
        return texture;
    }

    @NotNull
    @Override
    public Component getName() {
        return name;
    }

    @NotNull
    @Override
    public List<Component> getLore() {
        return lore;
    }

    @NotNull
    @Override
    public List<ButtonTicker> getTickers() {
        return tickers;
    }

    @Override
    public boolean getDefaultEnchanted() {
        return enchantedByDefault;
    }
}

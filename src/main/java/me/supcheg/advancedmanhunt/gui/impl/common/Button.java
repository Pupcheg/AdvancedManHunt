package me.supcheg.advancedmanhunt.gui.impl.common;

import lombok.CustomLog;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.text.Components;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@CustomLog
public abstract class Button implements AdvancedButton {
    protected final int slot;
    protected final BooleanController enableController;
    protected final BooleanController showController;
    protected final ResourceController<String> textureController;
    protected final ResourceController<Component> nameController;
    protected final ResourceController<List<Component>> loreController;
    protected final BooleanController enchantedController;
    protected final List<ButtonClickAction> clickActions;
    protected final Map<At, List<ButtonTicker>> tickConsumers;
    protected final ButtonTickContext context;
    protected boolean updated = true;

    public Button(@NotNull AdvancedGui gui, int slot, @NotNull AdvancedButtonBuilder builder) {
        this.slot = slot;
        this.enableController = new BooleanController(builder.getDefaultEnabled());
        this.showController = new BooleanController(builder.getDefaultShown());
        this.textureController = new ResourceController<>(builder.getTexture());
        this.nameController = new ResourceController<>(builder.getName());
        this.loreController = new ResourceController<>(builder.getLore());
        this.enchantedController = new BooleanController(builder.getDefaultEnchanted());
        this.clickActions = GuiCollections.sortAndTrim(builder.getClickActions());
        this.tickConsumers = GuiCollections.buildSortedConsumersMap(builder.getTickers());
        this.context = new ButtonTickContext(gui, this, slot);
    }

    public boolean pollUpdated() {
        boolean value = updated;
        updated = false;
        return value;
    }

    protected void updateUpdated() {
        updated = updated |
                enableController.pollUpdated() | showController.pollUpdated() |
                textureController.pollUpdated() | nameController.pollUpdated() |
                loreController.pollUpdated() | enchantedController.pollUpdated();
    }

    protected void acceptAllConsumersWithAt(@NotNull At at, @NotNull ButtonTickContext ctx) {
        for (ButtonTicker ticker : tickConsumers.get(at)) {
            try {
                ticker.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while accepting tick consumer", e);
            }
        }
    }

    protected void handleClick(@NotNull ButtonClickContext ctx) {
        if (isDisabled() || isHidden() || clickActions.isEmpty()) {
            return;
        }

        for (ButtonClickAction action : clickActions) {
            try {
                action.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while handling click to action", e);
            }
        }
    }

    @Override
    public void enableState(boolean value) {
        enableController.setState(value);
    }

    @Override
    public boolean isEnabled() {
        return enableController.getState();
    }

    @Override
    public void showState(boolean value) {
        showController.setState(value);
    }

    @Override
    public boolean isShown() {
        return showController.getState();
    }


    @Override
    public void addClickAction(@NotNull ButtonClickAction action) {
        clickActions.add(action);
    }

    @NotNull
    @Override
    public Collection<ButtonClickAction> getClickActions() {
        return clickActions;
    }

    @Override
    public void setTexture(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        textureController.setResource(path);
    }

    @Override
    public void setName(@NotNull Component name) {
        Objects.requireNonNull(name, "name");
        nameController.setResource(Components.removeItalic(name));
    }

    @Override
    public void setLore(@NotNull Component single) {
        Objects.requireNonNull(single, "single");
        loreController.setResource(Collections.singletonList(Components.removeItalic(single)));
    }

    @Override
    public void setLore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        loreController.setResource(Components.copyAndRemoveItalic(lore));
    }

    @Override
    public boolean isEnchanted() {
        return enchantedController.getState();
    }

    @Override
    public void setEnchanted(boolean value) {
        enchantedController.setState(value);
    }

    @NotNull
    @Override
    public AdvancedButtonBuilder toBuilderWithoutSlots() {
        AdvancedButtonBuilder builder = AdvancedButtonBuilder.button()
                .defaultEnabled(enableController.getInitialState())
                .defaultShown(showController.getInitialState())
                .texture(textureController.getResource())
                .name(nameController.getResource())
                .lore(loreController.getResource())
                .defaultEnchanted(enchantedController.getInitialState());

        builder.getClickActions().addAll(clickActions);
        for (List<ButtonTicker> values : tickConsumers.values()) {
            builder.getTickers().addAll(values);
        }

        return builder;
    }
}

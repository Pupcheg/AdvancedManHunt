package me.supcheg.advancedmanhunt.gui.impl;

import lombok.CustomLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonResourceGetContext;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.debug.ButtonDebugger;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@CustomLog
@RequiredArgsConstructor
public class DefaultAdvancedButton implements AdvancedButton {
    private final DefaultAdvancedGui gui;
    private final BooleanController enableController;
    private final BooleanController showController;
    private final ResourceController<ButtonTextureFunction, ButtonResourceGetContext, String> textureController;
    private final ResourceController<ButtonNameFunction, ButtonResourceGetContext, Component> nameController;
    private final ResourceController<ButtonLoreFunction, ButtonResourceGetContext, List<Component>> loreController;
    private final BooleanController enchantedController;
    private final List<ButtonClickAction> clickActions;
    private final Map<At, List<ButtonTicker>> tickConsumers;
    private final ButtonRenderer renderer;
    private boolean updated = true;

    private final ButtonDebugger debug = ButtonDebugger.create(this);

    public void tick(int slot) {
        ButtonResourceGetContext ctx = new ButtonResourceGetContext(gui, this, slot);

        acceptAllConsumersWithAt(At.TICK_START, ctx);

        textureController.tick(ctx);
        nameController.tick(ctx);
        loreController.tick(ctx);

        acceptAllConsumersWithAt(At.TICK_END, ctx);

        updated = updated |
                enableController.pollUpdated() | showController.pollUpdated() |
                textureController.pollUpdated() | nameController.pollUpdated() |
                loreController.pollUpdated() | enchantedController.pollUpdated();
    }

    public boolean pollUpdated() {
        boolean value = updated;
        updated = false;
        return value;
    }

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull ButtonResourceGetContext ctx) {
        for (ButtonTicker ticker : tickConsumers.get(at)) {
            try {
                ticker.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while accepting tick consumer", e);
            }
        }
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        handleClickNoDebug(event);
        debug.handlePostClick(event);
    }

    private void handleClickNoDebug(@NotNull InventoryClickEvent event) {
        if (isDisabled() || isHidden() || clickActions.isEmpty()) {
            return;
        }

        ButtonClickContext ctx = new ButtonClickContext(event, gui, this, event.getSlot(), (Player) event.getWhoClicked());

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
    public void setTexture(@NotNull ButtonTextureFunction function) {
        Objects.requireNonNull(function, "function");
        textureController.setFunction(function);
    }

    @Override
    public void setName(@NotNull ButtonNameFunction function) {
        Objects.requireNonNull(function, "function");
        nameController.setFunction(function);
    }

    @Override
    public void setLore(@NotNull ButtonLoreFunction function) {
        Objects.requireNonNull(function, "function");
        loreController.setFunction(function);
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
    public ItemStackHolder render() {
        return isHidden() ?
                renderer.emptyHolder() :
                renderer.render(
                        textureController.getResource(),
                        nameController.getResource(),
                        loreController.getResource(),
                        enchantedController.getState()
                );
    }

    @NotNull
    DefaultAdvancedButtonBuilder toBuilder() {
        DefaultAdvancedButtonBuilder builder = gui.getController().button();
        builder.defaultEnabled(enableController.getInitialState())
                .defaultShown(showController.getInitialState())
                .texture(textureController.getFunction())
                .name(nameController.getFunction())
                .lore(loreController.getFunction())
                .defaultEnchanted(enchantedController.getInitialState());

        builder.getClickActions().addAll(clickActions);
        for (List<ButtonTicker> values : tickConsumers.values()) {
            builder.getTickers().addAll(values);
        }

        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DefaultAdvancedButton that)) {
            return false;
        }

        return enableController.getInitialState() == that.enableController.getInitialState()
                && showController.getInitialState() == that.showController.getInitialState()
                && textureController.getFunction().equals(that.textureController.getFunction())
                && nameController.getFunction().equals(that.nameController.getFunction())
                && loreController.getFunction().equals(that.loreController.getFunction())
                && enchantedController.getInitialState() == that.enchantedController.getInitialState()
                && clickActions.equals(that.clickActions)
                && tickConsumers.equals(that.tickConsumers)
                && renderer.equals(that.renderer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                enableController.getInitialState(),
                showController.getInitialState(),
                textureController.getFunction(),
                nameController.getFunction(),
                loreController.getFunction(),
                enchantedController.getInitialState(),
                clickActions,
                tickConsumers,
                renderer
        );
    }
}

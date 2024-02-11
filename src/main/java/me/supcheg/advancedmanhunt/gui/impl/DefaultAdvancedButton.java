package me.supcheg.advancedmanhunt.gui.impl;

import lombok.CustomLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.render.ButtonRenderer;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.controller.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.controller.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.debug.ButtonDebugger;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
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
    private final ResourceController<String> textureController;
    private final ResourceController<Component> nameController;
    private final ResourceController<List<Component>> loreController;
    private final BooleanController enchantedController;
    private final List<ButtonClickAction> clickActions;
    private final Map<At, List<ButtonTicker>> tickConsumers;
    private final ButtonRenderer renderer;
    private boolean updated = true;

    private final ButtonDebugger debug = ButtonDebugger.create(this);

    public void tick(int slot) {
        ButtonTickContext ctx = new ButtonTickContext(gui, this, slot);

        acceptAllConsumersWithAt(At.TICK_START, ctx);
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

    private void acceptAllConsumersWithAt(@NotNull At at, @NotNull ButtonTickContext ctx) {
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

        ButtonClickContext ctx = wrapEvent(event);

        for (ButtonClickAction action : clickActions) {
            try {
                action.getConsumer().accept(ctx);
            } catch (Exception e) {
                log.error("An error occurred while handling click to action", e);
            }
        }
    }

    @NotNull
    private ButtonClickContext wrapEvent(@NotNull InventoryClickEvent event) {
        ButtonInteractType interactType = switch (event.getClick()) {
            case RIGHT, SHIFT_RIGHT -> ButtonInteractType.RIGHT_CLICK;
            default -> ButtonInteractType.LEFT_CLICK;
        };

        return new ButtonClickContext(
                gui, this,
                event.getSlot(),
                interactType,
                (Player) event.getWhoClicked()
        );

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
        nameController.setResource(ComponentUtil.removeItalic(name));
    }

    @Override
    public void setLore(@NotNull Component single) {
        Objects.requireNonNull(single, "single");
        loreController.setResource(Collections.singletonList(ComponentUtil.removeItalic(single)));
    }

    @Override
    public void setLore(@NotNull List<Component> lore) {
        Objects.requireNonNull(lore, "lore");
        loreController.setResource(ComponentUtil.copyAndRemoveItalic(lore));
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
                && textureController.getInitialResource().equals(that.textureController.getInitialResource())
                && nameController.getInitialResource().equals(that.nameController.getInitialResource())
                && loreController.getInitialResource().equals(that.loreController.getInitialResource())
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
                textureController.getInitialResource(),
                nameController.getInitialResource(),
                loreController.getInitialResource(),
                enchantedController.getInitialState(),
                clickActions,
                tickConsumers,
                renderer
        );
    }
}

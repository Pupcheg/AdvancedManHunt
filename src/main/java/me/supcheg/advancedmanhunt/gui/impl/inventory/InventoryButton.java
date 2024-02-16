package me.supcheg.advancedmanhunt.gui.impl.inventory;

import lombok.CustomLog;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonClickContext;
import me.supcheg.advancedmanhunt.gui.api.context.ButtonTickContext;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.impl.common.BooleanController;
import me.supcheg.advancedmanhunt.gui.impl.common.GuiCollections;
import me.supcheg.advancedmanhunt.gui.impl.common.ResourceController;
import me.supcheg.advancedmanhunt.gui.impl.inventory.debug.InventoryButtonDebugger;
import me.supcheg.advancedmanhunt.gui.impl.inventory.render.InventoryButtonRenderer;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.util.Components;
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
public class InventoryButton implements AdvancedButton {
    private final InventoryGui gui;
    private final BooleanController enableController;
    private final BooleanController showController;
    private final ResourceController<String> textureController;
    private final ResourceController<Component> nameController;
    private final ResourceController<List<Component>> loreController;
    private final BooleanController enchantedController;
    private final List<ButtonClickAction> clickActions;
    private final Map<At, List<ButtonTicker>> tickConsumers;
    private boolean updated = true;

    private final InventoryButtonRenderer renderer;

    private final InventoryButtonDebugger debug = InventoryButtonDebugger.create(this);

    public InventoryButton(@NotNull InventoryGui gui,
                           @NotNull InventoryButtonRenderer renderer,
                           @NotNull AdvancedButtonBuilder builder) {
        this.gui = gui;
        this.enableController = new BooleanController(builder.getDefaultEnabled());
        this.showController = new BooleanController(builder.getDefaultShown());
        this.textureController = new ResourceController<>(builder.getTexture());
        this.nameController = new ResourceController<>(builder.getName());
        this.loreController = new ResourceController<>(builder.getLore());
        this.enchantedController = new BooleanController(builder.getDefaultEnchanted());
        this.clickActions = GuiCollections.sortAndTrim(builder.getClickActions());
        this.tickConsumers = GuiCollections.buildSortedConsumersMap(builder.getTickers());

        this.renderer = renderer;
    }

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

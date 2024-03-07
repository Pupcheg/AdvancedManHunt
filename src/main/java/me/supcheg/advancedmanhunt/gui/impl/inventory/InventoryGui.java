package me.supcheg.advancedmanhunt.gui.impl.inventory;

import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.impl.common.Gui;
import me.supcheg.advancedmanhunt.gui.impl.common.logic.LogicDelegate;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class InventoryGui extends Gui {
    private final InventoryGuiController controller;
    private final Inventory inventory;
    private final InventoryButton[] slot2button;

    InventoryGui(@NotNull InventoryGuiController controller, @NotNull AdvancedGuiBuilder builder,
                 @NotNull LogicDelegate logicDelegate) {
        super(builder, logicDelegate);
        this.controller = controller;
        this.inventory = Bukkit.createInventory(new InventoryGuiHolder(this), rows * 9, Component.empty());
        this.slot2button = new InventoryButton[rows * 9];
        builder.getButtons().stream()
                .peek(builder.getButtonConfigurer()::configure)
                .forEach(this::addButton);
    }

    public void tick() {
        acceptAllConsumersWithAt(At.TICK_START, context);

        if (backgroundController.pollUpdated()) {
            String key = backgroundController.getResource();
            Component title = controller.getTextureWrapper().getGuiTexture(key).getComponent();

            for (HumanEntity viewer : inventory.getViewers()) {
                controller.getTitleSender().sendTitle(viewer.getOpenInventory(), title);
            }
        }

        for (int slot = 0; slot < slot2button.length; slot++) {
            InventoryButton button = slot2button[slot];

            if (button == null) {
                continue;
            }

            button.tick();

            if (button.pollUpdated()) {
                button.render().setAt(inventory, slot);
            }
        }

        acceptAllConsumersWithAt(At.TICK_END, context);
    }

    public void handleClick(@NotNull InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getSlot();

        if (clickedSlot < 0 || clickedSlot >= rows * 9) {
            return;
        }

        ClickType clickType = event.getClick();
        if (clickType == ClickType.DOUBLE_CLICK) {
            return;
        }

        InventoryButton button = slot2button[clickedSlot];

        if (button != null) {
            button.handleClick(event);
        }
    }

    protected void addButton(@NotNull AdvancedButtonBuilder builder) {
        if (builder.getSlots().isEmpty()) {
            throw new IllegalArgumentException("The button has no slots");
        }

        for (int slot : builder.getSlots()) {
            if (slot2button[slot] != null) {
                throw new IllegalStateException("Already has a button at " + slot);
            }
            slot2button[slot] = new InventoryButton(this, slot, builder);
        }
    }

    @Override
    public boolean open(@NotNull Player player) {
        InventoryView view = player.openInventory(inventory);
        if (view == null) {
            return false;
        }

        Component title = controller.getTextureWrapper().getGuiTexture(backgroundController.getResource()).getComponent();
        controller.getTitleSender().sendTitle(view, title);
        return true;
    }

    @Nullable
    @Override
    protected AdvancedButton @NotNull [] getButtons() {
        return slot2button;
    }
}

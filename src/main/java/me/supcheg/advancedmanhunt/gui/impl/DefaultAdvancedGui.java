package me.supcheg.advancedmanhunt.gui.impl;

import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.impl.builder.DefaultAdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.impl.controller.inventory.GuiInventoryController;
import me.supcheg.advancedmanhunt.gui.impl.controller.resource.GuiResourceController;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class DefaultAdvancedGui implements AdvancedGui {
    private final int rows;
    private final GuiInventoryController inventoryController;
    @Getter
    private final GuiResourceController<GuiBackgroundFunction, String> backgroundController;

    @Getter
    private final Map<DefaultAdvancedButton, IntSet> button2slots;
    private final DefaultAdvancedButton[] slot2button;

    public DefaultAdvancedGui(int rows, GuiInventoryController inventoryController, GuiResourceController<GuiBackgroundFunction, String> backgroundController) {
        this.rows = rows;
        this.inventoryController = inventoryController;
        this.backgroundController = backgroundController;

        this.button2slots = new HashMap<>();
        this.slot2button = new DefaultAdvancedButton[rows * 9];
    }

    public void tick() {
        inventoryController.tickGui(this);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        int clickedSlot = event.getSlot();

        if (clickedSlot < 0 || clickedSlot >= inventoryController.getSize()) {
            return;
        }

        DefaultAdvancedButton button = slot2button[clickedSlot];

        if (button != null) {
            button.handleClick((Player) event.getWhoClicked(), clickedSlot);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        inventoryController.handleInventoryClose((Player) event.getPlayer());
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public boolean isIndividual() {
        return inventoryController.isIndividual();
    }

    @Override
    public InventoryView open(Player player) {
        return inventoryController.open(player);
    }

    @Override
    public void addButton(AdvancedButtonBuilder buttonBuilder) {
        if (!(buttonBuilder instanceof DefaultAdvancedButtonBuilder defaultAdvancedButtonBuilder)) {
            throw new IllegalArgumentException();
        }

        IntSet slots = defaultAdvancedButtonBuilder.getSlots();
        DefaultAdvancedButton button = defaultAdvancedButtonBuilder.build(this);

        button2slots.put(button, slots);
        slots.forEach(i -> slot2button[i] = button);
    }

    @Override
    public void removeButton(int slot) {
        DefaultAdvancedButton button = slot2button[slot];
        if (button != null) {
            slot2button[slot] = null;
            button2slots.get(button).remove(slot);
        }
    }

    public void removeButtonFromAllSlots(DefaultAdvancedButton button) {
        IntSet slots = button2slots.remove(button);
        if (slots != null) {
            slots.forEach(slot -> slot2button[slot] = null);
        }
    }

    @Override
    public AdvancedButton getButtonAt(int slot) {
        return slot2button[slot];
    }

    @Override
    public void setBackground(String pngSubPath) {
        Objects.requireNonNull(pngSubPath, "pngSubPath");

        backgroundController.setFunction(GuiBackgroundFunction.constant(pngSubPath));
    }

    @Override
    public void animatedBackground(String pngSubPathTemplate, int size, Duration period) {
        Objects.requireNonNull(pngSubPathTemplate, "pngSubPathTemplate");
        Objects.requireNonNull(period, "period");

        GuiBackgroundFunction function = GuiBackgroundFunction.sizedAnimation(pngSubPathTemplate, size);
        backgroundController.setFunctionWithChangePeriod(function, period.getTicks());
    }

    @Override
    public void lazyBackground(GuiBackgroundFunction function) {
        Objects.requireNonNull(function, "function");
        backgroundController.setFunction(function);
    }

    @Override
    public void lazyAnimatedBackground(GuiBackgroundFunction function, Duration period) {
        Objects.requireNonNull(function, "function");
        Objects.requireNonNull(period, "period");
        backgroundController.setFunctionWithChangePeriod(function, period.getTicks());
    }
}

package me.supcheg.advancedmanhunt.gui.impl;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Data
public class AdvancedGuiHolder implements InventoryHolder {
    private DefaultAdvancedGui gui;

    @NotNull
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException();
    }
}

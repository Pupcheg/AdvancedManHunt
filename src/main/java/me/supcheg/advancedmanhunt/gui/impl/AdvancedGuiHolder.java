package me.supcheg.advancedmanhunt.gui.impl;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public class AdvancedGuiHolder implements InventoryHolder {
    private DefaultAdvancedGui gui;

    @NotNull
    @Contract("-> fail")
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException();
    }
}

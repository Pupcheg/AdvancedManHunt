package me.supcheg.advancedmanhunt.gui.impl.inventory;

import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
public class InventoryGuiHolder implements InventoryHolder {
    private InventoryGui gui;

    @NotNull
    @Contract("-> fail")
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException();
    }
}

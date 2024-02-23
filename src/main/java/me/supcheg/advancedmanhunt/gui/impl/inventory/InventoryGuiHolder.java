package me.supcheg.advancedmanhunt.gui.impl.inventory;

import com.google.errorprone.annotations.DoNotCall;
import lombok.Data;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Data
class InventoryGuiHolder implements InventoryHolder {
    private final InventoryGui gui;

    @NotNull
    @Contract("-> fail")
    @DoNotCall("guaranteed to throw an exception")
    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException();
    }
}

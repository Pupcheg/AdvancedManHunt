package me.supcheg.advancedmanhunt.injector.item;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public interface ItemStackHolder {
    void setAt(@NotNull Inventory inventory, int slot);
}

package me.supcheg.bridge.item;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ItemStackHolder {
    void setAt(@NotNull Inventory inventory, int slot);
}

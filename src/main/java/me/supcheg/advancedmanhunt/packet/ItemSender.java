package me.supcheg.advancedmanhunt.packet;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemSender {
    void sendItemStack(@NotNull InventoryView inventoryView, int slot, @NotNull ItemStack itemStack);
}

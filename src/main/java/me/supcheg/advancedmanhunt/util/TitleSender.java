package me.supcheg.advancedmanhunt.util;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface TitleSender {
    void sendTitle(@NotNull InventoryView inventoryView, @NotNull Component title);
}

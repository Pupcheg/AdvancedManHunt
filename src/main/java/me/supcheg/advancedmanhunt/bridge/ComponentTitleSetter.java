package me.supcheg.advancedmanhunt.bridge;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public interface ComponentTitleSetter {
    void setTitle(@NotNull InventoryView view, @NotNull Component title);
}

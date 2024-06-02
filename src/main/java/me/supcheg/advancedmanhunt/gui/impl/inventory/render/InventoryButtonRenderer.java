package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface InventoryButtonRenderer {
    @NotNull
    ItemStack render(@NotNull InventoryButton button);
}

package me.supcheg.advancedmanhunt.bridge;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Paper API doesn't support setting {@link Component} as title for {@link InventoryView}.
 * Implementations mimic such logic.
 */
public interface ComponentTitleSetter {
    void setTitle(@NotNull InventoryView view, @NotNull Component title);
}

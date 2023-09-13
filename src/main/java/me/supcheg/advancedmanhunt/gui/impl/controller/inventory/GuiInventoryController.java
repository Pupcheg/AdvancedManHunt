package me.supcheg.advancedmanhunt.gui.impl.controller.inventory;

import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuiInventoryController {
    @Nullable
    InventoryView open(@NotNull Player player);

    int getSize();

    boolean isIndividual();

    void tickGui(@NotNull DefaultAdvancedGui gui);

    void setTitle(@NotNull Player player, @NotNull Component component);

    void setItem(@NotNull Player player, int slot, @NotNull ItemStack itemStack);

    void handleInventoryClose(@NotNull Player player);
}

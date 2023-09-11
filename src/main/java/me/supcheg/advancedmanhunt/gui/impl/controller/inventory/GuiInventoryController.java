package me.supcheg.advancedmanhunt.gui.impl.controller.inventory;

import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public interface GuiInventoryController {
    InventoryView open(Player player);

    int getSize();

    boolean isIndividual();

    void tickGui(DefaultAdvancedGui gui);

    void setTitle(Player player, Component component);

    void setItem(Player player, int slot, ItemStack itemStack);

    void handleInventoryClose(Player player);
}

package me.supcheg.advancedmanhunt.gui.impl.controller.inventory;

import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class SharedGuiInventoryController implements GuiInventoryController {

    private final Inventory inventory;

    public SharedGuiInventoryController(int rows, InventoryHolder inventoryHolder) {
        this.inventory = Bukkit.createInventory(inventoryHolder, rows * 9, Component.empty());
    }

    @Override
    public int getSize() {
        return inventory.getSize();
    }

    @Override
    public InventoryView open(Player player) {
        return player.openInventory(inventory);
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    public void tickGui(DefaultAdvancedGui gui) {
        gui.getButton2slots().forEach((button, slots) -> {
            button.tick(slots, null);

            ItemStack rendered = button.render();

            for (int slot : slots) {
                inventory.setItem(slot, rendered);
            }
        });
    }

    @Override
    public void setTitle(Player player, Component component) {
        player.sendPlainMessage("Received title:");
        player.sendMessage(component);
    }

    @Override
    public void setItem(Player player, int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    @Override
    public void handleInventoryClose(Player player) {
    }
}

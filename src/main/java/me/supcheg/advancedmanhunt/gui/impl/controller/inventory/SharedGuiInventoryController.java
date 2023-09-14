package me.supcheg.advancedmanhunt.gui.impl.controller.inventory;

import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SharedGuiInventoryController implements GuiInventoryController {

    private final Inventory inventory;

    public SharedGuiInventoryController(int rows, @NotNull InventoryHolder inventoryHolder) {
        this.inventory = Bukkit.createInventory(inventoryHolder, rows * 9, Component.empty());
    }

    @Override
    public int getSize() {
        return inventory.getSize();
    }

    @Nullable
    @Override
    public InventoryView open(@NotNull Player player) {
        return player.openInventory(inventory);
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    public void tickGui(@NotNull DefaultAdvancedGui gui) {
        gui.getBackgroundController().tick(gui, null);

        DefaultAdvancedButton[] slot2button = gui.getSlot2button();
        for (int slot = 0; slot < slot2button.length; slot++) {
            DefaultAdvancedButton button = slot2button[slot];

            button.tick(slot, null);

            if (button.isUpdated()) {
                inventory.setItem(slot, button.render());
            }
        }
    }

    @Override
    public void setTitle(@NotNull Player player, @NotNull Component component) {
        player.sendPlainMessage("Received title:");
        player.sendMessage(component);
    }

    @Override
    public void handleInventoryClose(@NotNull Player player) {
    }
}

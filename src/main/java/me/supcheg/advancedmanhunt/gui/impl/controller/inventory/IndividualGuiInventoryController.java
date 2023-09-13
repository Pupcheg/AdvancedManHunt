package me.supcheg.advancedmanhunt.gui.impl.controller.inventory;

import lombok.Getter;
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

import java.util.Map;
import java.util.WeakHashMap;

public class IndividualGuiInventoryController implements GuiInventoryController {

    @Getter
    private final int size;
    private final InventoryHolder inventoryHolder;
    private final Map<Player, InventoryView> player2inventoryView;

    public IndividualGuiInventoryController(int rows, @NotNull InventoryHolder inventoryHolder) {
        this.size = rows * 9;
        this.inventoryHolder = inventoryHolder;
        this.player2inventoryView = new WeakHashMap<>();
    }

    @Nullable
    @Override
    public InventoryView open(@NotNull Player player) {
        Inventory inventory = Bukkit.createInventory(inventoryHolder, size, Component.empty());

        InventoryView inventoryView = player.openInventory(inventory);
        player2inventoryView.put(player, inventoryView);

        return inventoryView;
    }

    @Override
    public void handleInventoryClose(@NotNull Player player) {
        player2inventoryView.remove(player);
    }

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Override
    public void tickGui(@NotNull DefaultAdvancedGui gui) {
        gui.getButton2slots().forEach((button, slots) -> {
            for (Player player : player2inventoryView.keySet()) {
                gui.getBackgroundController().tick(gui, player);
                button.tick(slots, player);
            }

            if (!button.isUpdated()) {
                return;
            }

            ItemStack rendered = button.render();

            player2inventoryView.forEach((player, view) -> {
                Inventory inventory = view.getTopInventory();
                for (int slot : slots) {
                    inventory.setItem(slot, rendered);
                }
            });

        });

    }

    @Override
    public void setTitle(@NotNull Player player, @NotNull Component component) {
        player.sendPlainMessage("Received title:");
        player.sendMessage(component);
    }

    @Override
    public void setItem(@NotNull Player player, int slot, @NotNull ItemStack itemStack) {
        player2inventoryView.get(player).setItem(slot, itemStack);
    }
}

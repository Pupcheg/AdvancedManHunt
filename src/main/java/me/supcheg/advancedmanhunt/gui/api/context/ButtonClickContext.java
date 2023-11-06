package me.supcheg.advancedmanhunt.gui.api.context;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Data
public class ButtonClickContext {
    private final InventoryClickEvent event;
    private final AdvancedGui gui;
    private final AdvancedButton button;
    private final int slot;
    private final Player player;
}

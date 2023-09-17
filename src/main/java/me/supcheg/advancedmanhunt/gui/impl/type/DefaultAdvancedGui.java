package me.supcheg.advancedmanhunt.gui.impl.type;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public interface DefaultAdvancedGui extends AdvancedGui {
    void tick();

    void handleClick(@NotNull InventoryClickEvent event);

    void handleClose(@NotNull InventoryCloseEvent event);
}

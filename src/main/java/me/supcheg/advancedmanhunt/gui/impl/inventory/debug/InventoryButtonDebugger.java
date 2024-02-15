package me.supcheg.advancedmanhunt.gui.impl.inventory.debug;

import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface InventoryButtonDebugger {
    @NotNull
    static InventoryButtonDebugger create(@NotNull InventoryButton button) {
        return AdvancedManHuntConfig.get().debug ?
                new InventoryButtonDebuggerImpl(button) :
                DummyInventoryButtonDebugger.INSTANCE;
    }

    void handlePostClick(@NotNull InventoryClickEvent event);
}

package me.supcheg.advancedmanhunt.gui.impl.inventory.debug;

import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

public interface InventoryButtonDebugger {
    @NotNull
    static InventoryButtonDebugger create(@NotNull InventoryButton button) {
        return config().debug ?
                new InventoryButtonDebuggerImpl(button) :
                DummyInventoryButtonDebugger.INSTANCE;
    }

    void handlePostClick(@NotNull InventoryClickEvent event);
}

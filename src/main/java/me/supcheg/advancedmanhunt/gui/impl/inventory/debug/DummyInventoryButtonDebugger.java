package me.supcheg.advancedmanhunt.gui.impl.inventory.debug;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public enum DummyInventoryButtonDebugger implements InventoryButtonDebugger {
    INSTANCE;

    @Override
    public void handlePostClick(@NotNull InventoryClickEvent event) {
    }
}

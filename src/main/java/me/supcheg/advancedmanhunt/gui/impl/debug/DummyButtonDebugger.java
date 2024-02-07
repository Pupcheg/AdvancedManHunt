package me.supcheg.advancedmanhunt.gui.impl.debug;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public enum DummyButtonDebugger implements ButtonDebugger {
    INSTANCE;

    @Override
    public void handlePostClick(@NotNull InventoryClickEvent event) {
    }
}

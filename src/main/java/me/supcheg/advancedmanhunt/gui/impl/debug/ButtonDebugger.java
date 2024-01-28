package me.supcheg.advancedmanhunt.gui.impl.debug;

import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.gui.impl.DefaultAdvancedButton;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public interface ButtonDebugger {
    static ButtonDebugger create(@NotNull DefaultAdvancedButton button) {
        return AdvancedManHuntConfig.ENABLE_DEBUG ? new DefaultButtonDebugger(button) : new DummyButtonDebugger();
    }

    void handlePostClick(@NotNull InventoryClickEvent event);
}

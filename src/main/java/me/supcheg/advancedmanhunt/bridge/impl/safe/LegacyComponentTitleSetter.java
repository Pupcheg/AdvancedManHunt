package me.supcheg.advancedmanhunt.bridge.impl.safe;

import me.supcheg.advancedmanhunt.bridge.ComponentTitleSetter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class LegacyComponentTitleSetter implements ComponentTitleSetter {
    @Override
    public void setTitle(@NotNull InventoryView view, @NotNull Component title) {
        view.setTitle(LegacyComponentSerializer.legacySection().serialize(title));
    }
}

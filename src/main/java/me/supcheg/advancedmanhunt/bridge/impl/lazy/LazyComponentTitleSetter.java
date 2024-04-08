package me.supcheg.advancedmanhunt.bridge.impl.lazy;

import me.supcheg.advancedmanhunt.bridge.ComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.safe.LegacyComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsComponentTitleSetter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class LazyComponentTitleSetter extends LazyBridge<ComponentTitleSetter>
        implements ComponentTitleSetter {
    @Inject
    public LazyComponentTitleSetter() {
        super(ComponentTitleSetter.class,
                NmsComponentTitleSetter::new,
                LegacyComponentTitleSetter::new
        );
    }

    @Override
    public void setTitle(@NotNull InventoryView view, @NotNull Component title) {
        delegate().setTitle(view, title);
    }
}

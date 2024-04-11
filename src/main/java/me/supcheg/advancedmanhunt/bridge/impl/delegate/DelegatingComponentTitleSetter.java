package me.supcheg.advancedmanhunt.bridge.impl.delegate;

import me.supcheg.advancedmanhunt.bridge.ComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.nms.NmsComponentTitleSetter;
import me.supcheg.advancedmanhunt.bridge.impl.safe.LegacyComponentTitleSetter;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DelegatingComponentTitleSetter extends DelegatingBridge<ComponentTitleSetter>
        implements ComponentTitleSetter {
    @Inject
    public DelegatingComponentTitleSetter() {
        super(
                NmsComponentTitleSetter::new,
                LegacyComponentTitleSetter::new
        );
    }

    @Override
    public void setTitle(@NotNull InventoryView view, @NotNull Component title) {
        delegate.setTitle(view, title);
    }
}

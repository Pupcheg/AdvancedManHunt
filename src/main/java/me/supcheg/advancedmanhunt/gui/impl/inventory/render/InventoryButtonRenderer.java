package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface InventoryButtonRenderer {
    @NotNull
    ItemStackHolder render(@NotNull InventoryButton button);

    @NotNull
    ItemStackHolder emptyHolder();

    @NotNull
    @Contract("_, _ -> new")
    static InventoryButtonRenderer fromTextureWrapper(@NotNull ItemStackWrapperFactory wrapperFactory, @NotNull TextureWrapper textureWrapper) {
        return new ItemStackWrapperInventoryButtonRenderer(wrapperFactory, textureWrapper);
    }
}

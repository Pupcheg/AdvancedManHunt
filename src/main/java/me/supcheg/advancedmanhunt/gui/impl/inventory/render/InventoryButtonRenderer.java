package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import me.supcheg.advancedmanhunt.gui.impl.inventory.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
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

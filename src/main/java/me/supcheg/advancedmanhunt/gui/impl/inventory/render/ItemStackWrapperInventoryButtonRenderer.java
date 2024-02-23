package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import me.supcheg.advancedmanhunt.gui.impl.inventory.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ItemStackWrapperInventoryButtonRenderer implements InventoryButtonRenderer {
    private final ItemStackWrapperFactory itemStackWrapperFactory;
    private final TextureWrapper textureWrapper;

    @NotNull
    @Override
    public ItemStackHolder render(@NotNull InventoryButton button) {
        ItemStackWrapper wrapper = itemStackWrapperFactory.createItemStackWrapper();
        wrapper.setMaterial("minecraft:paper");
        wrapper.setCustomModelData(textureWrapper.getPaperCustomModelData(button.getTextureController().getResource()));
        wrapper.setTitle(button.getNameController().getResource());
        wrapper.setLore(button.getLoreController().getResource());
        wrapper.setEnchanted(button.getEnchantedController().getState());

        return wrapper.createSnapshotHolder();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyHolder() {
        return itemStackWrapperFactory.emptyItemStackHolder();
    }
}

package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class ItemStackWrapperInventoryButtonRenderer implements InventoryButtonRenderer {
    private static final String PAPER_KEY = "minecraft:paper";
    private final ItemStackWrapperFactory itemStackWrapperFactory;
    private final TextureWrapper textureWrapper;

    @NotNull
    @Override
    public ItemStackHolder render(@NotNull InventoryButton button) {
        String textureResourceKey = button.getTextureController().getResource();
        int customModelData = textureWrapper.getPaperTexture(textureResourceKey).getCustomModelData();
        Component name = button.getNameController().getResource();
        List<Component> lore = button.getLoreController().getResource();
        boolean isEnchanted = button.getEnchantedController().getState();

        ItemStackWrapper wrapper = itemStackWrapperFactory.createItemStackWrapper();

        wrapper.setMaterial(PAPER_KEY);
        wrapper.setCustomModelData(customModelData);
        wrapper.setTitle(name);
        wrapper.setLore(lore);
        wrapper.setEnchanted(isEnchanted);

        return wrapper.createSnapshotHolder();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyHolder() {
        return itemStackWrapperFactory.emptyItemStackHolder();
    }
}

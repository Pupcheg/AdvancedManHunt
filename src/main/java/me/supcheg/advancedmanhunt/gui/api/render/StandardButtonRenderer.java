package me.supcheg.advancedmanhunt.gui.api.render;

import lombok.RequiredArgsConstructor;
import me.supcheg.bridge.item.ItemStackHolder;
import me.supcheg.bridge.item.ItemStackWrapper;
import me.supcheg.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class StandardButtonRenderer implements ButtonRenderer {
    private final ItemStackWrapperFactory itemStackWrapperFactory;
    private final TextureWrapper textureWrapper;

    @NotNull
    @Override
    public ItemStackHolder render(@NotNull String texture, @NotNull Component name, @NotNull List<Component> lore, boolean enchanted) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(lore, "lore");

        ItemStackWrapper wrapper = itemStackWrapperFactory.createItemStackWrapper();
        wrapper.setMaterial("minecraft:paper");
        wrapper.setCustomModelData(textureWrapper.getPaperCustomModelData(texture));
        wrapper.setTitle(name);
        wrapper.setLore(lore);
        wrapper.setEnchanted(enchanted);

        return wrapper.createSnapshotHolder();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyHolder() {
        return itemStackWrapperFactory.emptyItemStackHolder();
    }
}

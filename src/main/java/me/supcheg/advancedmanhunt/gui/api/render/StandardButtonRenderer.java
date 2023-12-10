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
    private ItemStackHolder itemStack;
    private int lastHash;

    @NotNull
    @Override
    public ItemStackHolder render(@NotNull String texture, @NotNull Component name, @NotNull List<Component> lore, boolean enchanted) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(lore, "lore");

        int curHash = Objects.hash(texture, name, lore, enchanted);
        if (itemStack == null || curHash != lastHash) {
            lastHash = curHash;
            ItemStackWrapper wrapper = itemStackWrapperFactory.createItemStackWrapper();
            wrapper.setCustomModelData(textureWrapper.getPaperCustomModelData(texture));
            wrapper.setTitle(name);
            wrapper.setLore(lore);
            wrapper.setEnchanted(enchanted);
            itemStack = wrapper.createSnapshotHolder();
        }
        return itemStack;
    }

    @NotNull
    @Override
    public ItemStackHolder emptyHolder() {
        return itemStackWrapperFactory.emptyItemStackHolder();
    }
}

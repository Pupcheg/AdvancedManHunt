package me.supcheg.bridge.item;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemStackWrapper {
    void setTitle(@NotNull Component title);

    void setLore(@NotNull List<Component> lore);

    void setMaterial(@NotNull String key);

    void setCustomModelData(int customModelData);

    void setEnchanted(boolean value);

    @NotNull
    ItemStackHolder createSnapshotHolder();

    @NotNull
    ItemStackHolder createDynamicHolder();
}

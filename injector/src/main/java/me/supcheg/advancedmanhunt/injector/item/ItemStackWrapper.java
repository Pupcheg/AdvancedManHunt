package me.supcheg.advancedmanhunt.injector.item;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemStackWrapper {
    void setTitle(@NotNull Component title);

    void setLore(@NotNull List<Component> lore);

    void setMaterial(@NotNull String key);

    void setCustomModelData(@Nullable Integer customModelData);

    void setEnchanted(boolean value);

    @NotNull
    ItemStackHolder createSnapshotHolder();
}

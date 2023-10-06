package me.supcheg.advancedmanhunt.gui.api.render;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ButtonRenderer {
    @Nullable
    ItemStack render(@NotNull String texture, @NotNull Component name, @NotNull List<Component> lore, boolean enchanted);

    @NotNull
    @Contract("_ -> new")
    static ButtonRenderer fromTextureWrapper(@NotNull TextureWrapper textureWrapper) {
        return new StandardButtonRenderer(textureWrapper);
    }
}

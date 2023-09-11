package me.supcheg.advancedmanhunt.gui.api.render;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@FunctionalInterface
public interface ButtonRenderer {
    ItemStack render(String texture, Component name, List<Component> lore, boolean enchanted);

    static ButtonRenderer standardFromTextureWrapper(TextureWrapper textureWrapper) {
        return new StandardButtonRenderer(textureWrapper);
    }
}

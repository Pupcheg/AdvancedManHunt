package me.supcheg.advancedmanhunt.gui.impl.inventory.render;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.TextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BukkitInventoryButtonRenderer implements InventoryButtonRenderer {
    private final TextureWrapper textureWrapper;

    @NotNull
    @Override
    public ItemStack render(@NotNull InventoryButton button) {
        String textureResourceKey = button.getTextureController().getResource();
        int customModelData = textureWrapper.getPaperTexture(textureResourceKey).getCustomModelData();
        Component name = button.getNameController().getResource();
        List<Component> lore = button.getLoreController().getResource();
        boolean isEnchanted = button.getEnchantedController().getState();

        ItemStack itemStack = new ItemStack(Material.PAPER);

        ItemMeta meta = itemStack.getItemMeta();
        Objects.requireNonNull(meta, "meta");

        meta.displayName(name);
        meta.lore(lore);
        meta.setCustomModelData(customModelData);
        if (isEnchanted) {
            meta.addEnchant(Enchantment.PROTECTION, 1, true);
        }

        return itemStack;
    }
}

package me.supcheg.advancedmanhunt.gui.api.render;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class StandardButtonRenderer implements ButtonRenderer {
    private static final Enchantment INVISIBLE_ENCHANTMENT = new EnchantmentWrapper("dummy_enchantment");

    private final TextureWrapper textureWrapper;
    private ItemStack itemStack;
    private int lastHash;


    public ItemStack render(String texture, Component name, List<Component> lore, boolean enchanted) {
        int curHash = Objects.hash(texture, name, lore, enchanted);
        if (itemStack == null || curHash != lastHash) {
            lastHash = curHash;
            itemStack = new ItemStack(Material.PAPER);

            ItemMeta itemMeta = itemStack.getItemMeta();
            Objects.requireNonNull(itemMeta, "itemMeta");

            itemMeta.setCustomModelData(textureWrapper.getPaperCustomModelData(texture));
            itemMeta.displayName(name);
            itemMeta.lore(lore);
            if (enchanted) {
                itemMeta.addEnchant(INVISIBLE_ENCHANTMENT, 0, true);
            }

            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}

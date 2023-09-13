package me.supcheg.advancedmanhunt.gui.api.render;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class StandardButtonRenderer implements ButtonRenderer {
    private static final Enchantment INVISIBLE_ENCHANTMENT = new EnchantmentWrapper("dummy_enchantment");

    private final TextureWrapper textureWrapper;
    private ItemStack itemStack;
    private int lastHash;


    @Nullable
    public ItemStack render(@NotNull String texture, @NotNull Component name, @NotNull List<Component> lore, boolean enchanted) {
        Objects.requireNonNull(texture, "texture");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(lore, "lore");

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

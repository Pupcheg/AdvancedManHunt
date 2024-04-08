package me.supcheg.advancedmanhunt.bridge.impl.safe;

import lombok.Setter;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BukkitItemStackWrapperFactory implements ItemStackWrapperFactory {
    private final ItemStackHolder emptyHolder = (inv, slot) -> inv.setItem(slot, null);

    @NotNull
    @Override
    public ItemStackWrapper createItemStackWrapper() {
        return new BukkitItemStackWrapper();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyItemStackHolder() {
        return emptyHolder;
    }

    @Setter(onMethod_ = {@Override})
    private static class BukkitItemStackWrapper implements ItemStackWrapper {
        private String key;
        private Component title;
        private List<Component> lore;
        private Integer customModelData;
        private boolean enchanted;

        @NotNull
        public ItemStack buildItemStack() {
            Objects.requireNonNull(key, "key");

            Material material = Material.matchMaterial(key);
            Objects.requireNonNull(material, "Unknown material: " + key);

            ItemStack itemStack = new ItemStack(material);

            if (itemStack.hasItemMeta()) {
                ItemMeta meta = itemStack.getItemMeta();

                meta.displayName(title);
                meta.lore(lore);
                meta.setCustomModelData(customModelData);
                if (enchanted) {
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
                }

                itemStack.setItemMeta(meta);
            }

            return itemStack;
        }

        @NotNull
        @Override
        public ItemStackHolder createSnapshotHolder() {
            ItemStack itemStack = buildItemStack();
            return (inv, slot) -> inv.setItem(slot, itemStack);
        }
    }
}

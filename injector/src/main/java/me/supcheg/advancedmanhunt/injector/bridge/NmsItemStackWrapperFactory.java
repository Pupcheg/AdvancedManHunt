package me.supcheg.advancedmanhunt.injector.bridge;

import com.destroystokyo.paper.util.SneakyThrow;
import me.supcheg.bridge.item.ItemStackHolder;
import me.supcheg.bridge.item.ItemStackWrapper;
import me.supcheg.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class NmsItemStackWrapperFactory implements ItemStackWrapperFactory {
    private final MethodHandle craftInventory_getContainer =
            CraftBukkitResolver.resolveMethodInClass("inventory.CraftInventory", "getInventory");
    private final ItemStackHolder EMPTY_HOLDER = (inv, slot) -> getContainer(inv).setItem(slot, ItemStack.EMPTY);

    @NotNull
    @Override
    public ItemStackWrapper createItemStackWrapper() {
        return new NmsItemStackWrapper();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyItemStackHolder() {
        return EMPTY_HOLDER;
    }

    private class NmsItemStackWrapper implements ItemStackWrapper {
        private Component title;
        private List<Component> lore;
        private String materialKey;
        private Integer customModelData;
        private boolean enchanted;

        @Override
        public void setTitle(@NotNull Component title) {
            this.title = title;
        }

        @Override
        public void setLore(@NotNull List<Component> lore) {
            this.lore = lore;
        }

        @Override
        public void setMaterial(@NotNull String key) {
            this.materialKey = key;
        }

        @Override
        public void setCustomModelData(int customModelData) {
            this.customModelData = customModelData;
        }

        @Override
        public void setEnchanted(boolean value) {
            this.enchanted = value;
        }

        @NotNull
        public ItemStack buildItemStack() {
            ItemStack itemStack = new ItemStack(getItemByKey(materialKey));

            CompoundTag itemTag = itemStack.getOrCreateTag();
            CompoundTag displayTag = itemStack.getOrCreateTagElement(ItemStack.TAG_DISPLAY);
            if (title != null) {
                displayTag.put(ItemStack.TAG_DISPLAY_NAME, toTag(title));
            }

            if (this.lore != null) {
                displayTag.put(ItemStack.TAG_LORE, createStringList(lore));
            }

            if (customModelData != null) {
                itemTag.putInt("CustomModelData", customModelData);
            }

            if (enchanted) {
                itemTag.put(ItemStack.TAG_ENCH, createEnchantmentsList());
            }

            return itemStack;
        }

        @NotNull
        private ListTag createEnchantmentsList() {
            ListTag enchantments = new ListTag();
            CompoundTag subtag = new CompoundTag();

            subtag.putString("id", "0");
            subtag.putShort("lvl", (short) 0);

            enchantments.add(subtag);
            return enchantments;
        }

        @NotNull
        private ListTag createStringList(List<Component> list) {
            ListTag tagList = new ListTag();
            for (Component value : list) {
                tagList.add(toTag(value));
            }

            return tagList;
        }

        @NotNull
        @Override
        public ItemStackHolder createSnapshotHolder() {
            ItemStack itemStack = buildItemStack();
            return (inv, slot) -> getContainer(inv).setItem(slot, itemStack);
        }

        @NotNull
        @Override
        public ItemStackHolder createDynamicHolder() {
            return (inv, slot) -> getContainer(inv).setItem(slot, buildItemStack());
        }
    }

    @NotNull
    private StringTag toTag(@NotNull Component component) {
        return StringTag.valueOf(GsonComponentSerializer.gson().serialize(component));
    }

    @NotNull
    private Item getItemByKey(@NotNull String key) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(key));
    }

    @NotNull
    private Container getContainer(@NotNull Inventory inventory) {
        try {
            return (Container) craftInventory_getContainer.invoke(inventory);
        } catch (Throwable thr) {
            SneakyThrow.sneaky(thr);
            return null;
        }
    }
}

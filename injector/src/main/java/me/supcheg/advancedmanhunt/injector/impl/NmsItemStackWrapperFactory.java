package me.supcheg.advancedmanhunt.injector.impl;

import lombok.Setter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.injector.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
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

import java.util.List;
import java.util.Objects;

import static me.supcheg.advancedmanhunt.injector.ReflectiveAccessor.craftInventory_getInventory;

public class NmsItemStackWrapperFactory implements ItemStackWrapperFactory {
    private final ItemStackHolder EMPTY_HOLDER = (inv, slot) -> getContainer(inv).setItem(slot, ItemStack.EMPTY);
    private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";

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

    @Setter(onMethod_ = {@Override})
    private static class NmsItemStackWrapper implements ItemStackWrapper {
        private Component title;
        private List<Component> lore;
        private String key;
        private Integer customModelData;
        private boolean enchanted;

        @NotNull
        public ItemStack buildItemStack() {
            Objects.requireNonNull(key, "key");

            ItemStack itemStack = new ItemStack(getItemByKey(key));

            if (title != null) {
                itemStack.getOrCreateTagElement(ItemStack.TAG_DISPLAY)
                        .put(ItemStack.TAG_DISPLAY_NAME, toTag(title));
            }

            if (lore != null) {
                itemStack.getOrCreateTagElement(ItemStack.TAG_DISPLAY)
                        .put(ItemStack.TAG_LORE, createStringList(lore));
            }

            if (customModelData != null) {
                itemStack.getOrCreateTag()
                        .putInt(TAG_CUSTOM_MODEL_DATA, customModelData);
            }

            if (enchanted) {
                itemStack.getOrCreateTag()
                        .put(ItemStack.TAG_ENCH, createEnchantmentsList());
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
        private ListTag createStringList(@NotNull List<Component> list) {
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
    }

    @NotNull
    private static StringTag toTag(@NotNull Component component) {
        return StringTag.valueOf(GsonComponentSerializer.gson().serialize(component));
    }

    @NotNull
    private static Item getItemByKey(@NotNull String key) {
        return BuiltInRegistries.ITEM.get(new ResourceLocation(key));
    }

    @SneakyThrows
    @NotNull
    private static Container getContainer(@NotNull Inventory inventory) {
        return (Container) craftInventory_getInventory.invoke(inventory);
    }
}

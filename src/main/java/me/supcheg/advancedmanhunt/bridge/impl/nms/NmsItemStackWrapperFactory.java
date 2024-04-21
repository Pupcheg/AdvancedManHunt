package me.supcheg.advancedmanhunt.bridge.impl.nms;

import lombok.Setter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackHolder;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapper;
import me.supcheg.advancedmanhunt.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static me.supcheg.advancedmanhunt.reflect.ReflectAccessors.CRAFT_BUKKIT;

public class NmsItemStackWrapperFactory implements ItemStackWrapperFactory {
    private static final String TAG_CUSTOM_MODEL_DATA = "CustomModelData";
    private final ItemStackHolder emptyHolder;
    private final ListTag emptyEnchantments;
    private final MethodHandle craftInventory_getInventory;

    public NmsItemStackWrapperFactory() {
        this.emptyHolder = (inv, slot) -> getContainer(inv).setItem(slot, ItemStack.EMPTY);
        this.emptyEnchantments = createEmptyEnchantments();
        this.craftInventory_getInventory = CRAFT_BUKKIT.resolveMethod("inventory.CraftInventory", "getInventory");
    }

    @NotNull
    private static ListTag createEmptyEnchantments() {
        CompoundTag enchantment = new CompoundTag();
        enchantment.putString("id", "0");
        enchantment.putShort("lvl", (short) 0);
        return new ListTag(Collections.singletonList(enchantment), Tag.TAG_COMPOUND);
    }

    @NotNull
    @Override
    public ItemStackWrapper createItemStackWrapper() {
        return new NmsItemStackWrapper();
    }

    @NotNull
    @Override
    public ItemStackHolder emptyItemStackHolder() {
        return emptyHolder;
    }

    @Setter(onMethod_ = {@Override})
    private class NmsItemStackWrapper implements ItemStackWrapper {
        private String key;
        private Component title;
        private List<Component> lore;
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
                        .put(ItemStack.TAG_LORE, toTagList(lore));
            }

            if (customModelData != null) {
                itemStack.getOrCreateTag()
                        .putInt(TAG_CUSTOM_MODEL_DATA, customModelData);
            }

            if (enchanted) {
                itemStack.getOrCreateTag()
                        .put(ItemStack.TAG_ENCH, emptyEnchantments);
            }

            return itemStack;
        }

        @NotNull
        @Override
        public ItemStackHolder createSnapshotHolder() {
            ItemStack itemStack = buildItemStack();
            return (inv, slot) -> getContainer(inv).setItem(slot, itemStack);
        }
    }

    @NotNull
    private static ListTag toTagList(@NotNull List<Component> list) {
        List<Tag> tags = new ArrayList<>(list.size());
        for (Component value : list) {
            tags.add(toTag(value));
        }
        return new ListTag(tags, Tag.TAG_STRING);
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
    private Container getContainer(@NotNull Inventory inventory) {
        return (Container) craftInventory_getInventory.invoke(inventory);
    }
}

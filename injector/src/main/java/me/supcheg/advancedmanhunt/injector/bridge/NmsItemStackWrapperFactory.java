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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Objects;

public class NmsItemStackWrapperFactory implements ItemStackWrapperFactory {
    private final MethodHandle craftInventory_getContainer =
            CraftBukkitResolver.resolveMethodInClass("inventory.CraftInventory", "getInventory");
    private final MethodHandle craftPlayer_getHandle =
            CraftBukkitResolver.resolveMethodInClass("entity.CraftPlayer", "getHandle");
    private final ItemStackHolder EMPTY_HOLDER = new NmsItemStackHolder(ItemStack.EMPTY);

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
        public void setCustomModelData(@Nullable Integer customModelData) {
            this.customModelData = customModelData;
        }

        @Override
        public void setEnchanted(boolean value) {
            this.enchanted = value;
        }

        @NotNull
        public ItemStack buildItemStack() {
            Objects.requireNonNull(materialKey, "materialKey");

            ItemStack itemStack = new ItemStack(getItemByKey(materialKey));

            CompoundTag itemTag = itemStack.getOrCreateTag();
            CompoundTag displayTag = itemStack.getOrCreateTagElement(net.minecraft.world.item.ItemStack.TAG_DISPLAY);
            if (title != null) {
                displayTag.put(net.minecraft.world.item.ItemStack.TAG_DISPLAY_NAME, toTag(title));
            }

            if (lore != null) {
                displayTag.put(net.minecraft.world.item.ItemStack.TAG_LORE, createStringList(lore));
            }

            if (customModelData != null) {
                itemTag.putInt("CustomModelData", customModelData);
            }

            if (enchanted) {
                itemTag.put(net.minecraft.world.item.ItemStack.TAG_ENCH, createEnchantmentsList());
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
            return new NmsItemStackHolder(itemStack);
        }
    }

    private class NmsItemStackHolder implements ItemStackHolder {
        private final ItemStack itemStack;

        public NmsItemStackHolder(@NotNull ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public void setAt(@NotNull Inventory inv, int slot) {
            getContainer(inv).setItem(slot, itemStack);
        }

        @Override
        public void sendAt(@NotNull Player player, int rawSlot) {
            sendItemStack(player, itemStack, rawSlot);
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

    private void sendItemStack(@NotNull Player player, @NotNull ItemStack itemStack, int rawSlot) {
        try {
            ServerPlayer handle = (ServerPlayer) craftPlayer_getHandle.invoke(player);
            handle.containerSynchronizer.sendSlotChange(handle.containerMenu, rawSlot, itemStack);
        } catch (Throwable thr) {
            SneakyThrow.sneaky(thr);
        }
    }
}

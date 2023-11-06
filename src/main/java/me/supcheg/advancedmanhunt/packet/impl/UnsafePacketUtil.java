package me.supcheg.advancedmanhunt.packet.impl;

import com.google.gson.JsonElement;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.packet.PacketUtil;
import me.supcheg.advancedmanhunt.util.UnsafeNMS;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

public class UnsafePacketUtil implements PacketUtil {
    private final MethodHandle craftHumanEntity_getHandle;
    private final MethodHandle entityPlayer_container;
    private final MethodHandle container_containerId;
    private final MethodHandle container_stateId;

    private final MethodHandle craftContainer_getNotchInventoryType;
    private final MethodHandle packetPlayOutOpenWindow_constructor;
    private final MethodHandle packetPlayOutSetSlot_constructor;

    private final MethodHandle entityPlayer_playerConnection;
    private final MethodHandle playerConnection_send;

    private final MethodHandle chatSerializer_deserialize;

    private final MethodHandle craftItemStack_asNMSCopy;

    @SneakyThrows
    public UnsafePacketUtil() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        String craftBukkit = UnsafeNMS.locateCraftBukkit();

        Class<?> craftHumanEntityClass = Class.forName(craftBukkit + ".entity.CraftHumanEntity");
        craftHumanEntity_getHandle = lookup.unreflect(craftHumanEntityClass.getMethod("getHandle"));

        Class<?> entityPlayerClass = Class.forName("net.minecraft.server.level.EntityPlayer");
        entityPlayer_container = lookup.unreflectGetter(entityPlayerClass.getField("bS"));

        Class<?> containerClass = Class.forName("net.minecraft.world.inventory.Container");
        container_containerId = lookup.unreflectGetter(containerClass.getField("j"));
        container_stateId = lookup.unreflect(containerClass.getMethod("k"));

        Class<?> craftContainerClass = Class.forName(craftBukkit + ".inventory.CraftContainer");
        craftContainer_getNotchInventoryType = lookup.unreflect(craftContainerClass.getMethod("getNotchInventoryType", Inventory.class));

        Class<?> packetPlayOutOpenWindowClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutOpenWindow");
        packetPlayOutOpenWindow_constructor = lookup.unreflectConstructor(packetPlayOutOpenWindowClass.getConstructors()[0]);

        Class<?> packetPlayOutSetSlotClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutSetSlot");
        packetPlayOutSetSlot_constructor = lookup.unreflectConstructor(packetPlayOutSetSlotClass.getConstructors()[0]);

        entityPlayer_playerConnection = lookup.unreflectGetter(entityPlayerClass.getField("c"));

        Class<?> playerConnectionClass = Class.forName("net.minecraft.server.network.PlayerConnection");
        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.Packet");
        playerConnection_send = lookup.unreflect(playerConnectionClass.getMethod("b", packetClass));

        Class<?> chatSerializerClass = Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
        chatSerializer_deserialize = lookup.unreflect(chatSerializerClass.getMethod("a", JsonElement.class));

        Class<?> craftItemStackClass = Class.forName(craftBukkit + ".inventory.CraftItemStack");
        craftItemStack_asNMSCopy = lookup.unreflect(craftItemStackClass.getMethod("asNMSCopy", ItemStack.class));
    }

    @SneakyThrows
    @Override
    public void sendTitle(@NotNull InventoryView inventoryView, @NotNull Component title) {
        Player player = (Player) inventoryView.getPlayer();

        Object containerId = getOpenedContainerId(player);
        Object windowType = craftContainer_getNotchInventoryType.invoke(inventoryView.getTopInventory());

        Object packet = packetPlayOutOpenWindow_constructor.invoke(containerId, windowType, asChatComponent(title));
        sendPacket(player, packet);

        //noinspection UnstableApiUsage
        player.updateInventory();
    }

    @SneakyThrows
    @Override
    public void sendItemStack(@NotNull InventoryView inventoryView, int slot, @NotNull ItemStack itemStack) {
        Player player = (Player) inventoryView.getPlayer();

        Object containerId = getOpenedContainerId(player);
        Object stateId = getOpenedContainerNextStateId(player);
        Object nmsItemStack = asNMSItemStack(itemStack);

        Object packet = packetPlayOutSetSlot_constructor.invoke(containerId, stateId, slot, nmsItemStack);
        sendPacket(player, packet);
    }

    @SneakyThrows
    private void sendPacket(@NotNull Player player, @NotNull Object packet) {
        Object entityPlayer = craftHumanEntity_getHandle.invoke(player);
        Object playerConnection = entityPlayer_playerConnection.invoke(entityPlayer);
        playerConnection_send.invoke(playerConnection, packet);
    }

    @SneakyThrows
    @NotNull
    @Contract("_ -> new")
    private Object getOpenedContainerId(@NotNull Player player) {
        Object entityPlayer = craftHumanEntity_getHandle.invoke(player);
        Object container = entityPlayer_container.invoke(entityPlayer);
        return container_containerId.invoke(container);
    }

    @SneakyThrows
    @NotNull
    @Contract("_ -> new")
    private Object getOpenedContainerNextStateId(@NotNull Player player) {
        Object entityPlayer = craftHumanEntity_getHandle.invoke(player);
        Object container = entityPlayer_container.invoke(entityPlayer);
        return container_stateId.invoke(container);
    }

    @SneakyThrows
    @NotNull
    private Object asNMSItemStack(@NotNull ItemStack bukkitItemStack) {
        return craftItemStack_asNMSCopy.invoke(bukkitItemStack);
    }

    @SneakyThrows
    @NotNull
    @Contract("_ -> new")
    private Object asChatComponent(@NotNull Component adventure) {
        return chatSerializer_deserialize.invoke(GsonComponentSerializer.gson().serializeToTree(adventure));
    }
}

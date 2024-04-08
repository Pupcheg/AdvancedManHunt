package me.supcheg.advancedmanhunt.bridge.impl.nms;

import io.papermc.paper.adventure.AdventureComponent;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.ComponentTitleSetter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import static me.supcheg.advancedmanhunt.bridge.impl.nms.ReflectiveAccessor.resolveCraftBukkitFieldSetter;
import static me.supcheg.advancedmanhunt.bridge.impl.nms.ReflectiveAccessor.resolveCraftBukkitMethod;

public class NmsComponentTitleSetter implements ComponentTitleSetter {

    private final MethodHandle craftPlayer_getHandle;
    private final MethodHandle craftContainer_getNotchInventoryType;
    private final MethodHandle craftInventoryView_setTitle;
    private final MethodHandle abstractContainerMenu_setTitle;

    public NmsComponentTitleSetter() {
        craftPlayer_getHandle =
                resolveCraftBukkitMethod("entity.CraftPlayer", "getHandle");
        craftContainer_getNotchInventoryType =
                resolveCraftBukkitMethod("inventory.CraftContainer", "getNotchInventoryType", Inventory.class);
        craftInventoryView_setTitle =
                resolveCraftBukkitFieldSetter("inventory.CraftInventoryView", "title");
        abstractContainerMenu_setTitle =
                resolveTitleSetter();
    }

    @SneakyThrows
    @NotNull
    private static MethodHandle resolveTitleSetter() {
        Field title = AbstractContainerMenu.class.getDeclaredField("title");
        title.setAccessible(true);
        return MethodHandles.lookup().unreflectSetter(title);
    }

    @SneakyThrows
    @Override
    public void setTitle(@NotNull InventoryView view, @NotNull Component title) {
        ServerPlayer handle = (ServerPlayer) craftPlayer_getHandle.invoke(view.getPlayer());

        int containerId = handle.containerMenu.containerId;
        MenuType<?> type = (MenuType<?>) craftContainer_getNotchInventoryType.invoke(view.getTopInventory());

        AdventureComponent minecraftTitle = new AdventureComponent(title);
        ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, type, minecraftTitle);

        handle.connection.send(packet);
        handle.containerMenu.sendAllDataToRemote();

        abstractContainerMenu_setTitle.invoke(handle.containerMenu, minecraftTitle);

        String rawTitle = PlainTextComponentSerializer.plainText().serialize(title);
        craftInventoryView_setTitle.invoke(view, rawTitle);
    }
}

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
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;

import static me.supcheg.advancedmanhunt.reflect.ReflectAccessors.CRAFT_BUKKIT;
import static me.supcheg.advancedmanhunt.reflect.ReflectAccessors.NO_CHANGES;

public class NmsComponentTitleSetter implements ComponentTitleSetter {
    private final MethodHandle craftPlayer_getHandle;
    private final MethodHandle abstractContainerMenu_setTitle;
    private final MethodHandle craftInventoryView_setTitle;

    public NmsComponentTitleSetter() {
        craftPlayer_getHandle = CRAFT_BUKKIT.resolveMethod("entity.CraftPlayer", "getHandle");
        abstractContainerMenu_setTitle = NO_CHANGES.resolveFieldSetter(AbstractContainerMenu.class, "title");
        craftInventoryView_setTitle = CRAFT_BUKKIT.resolveFieldSetter("inventory.CraftInventoryView", "title");
    }

    @SneakyThrows
    @Override
    public void setTitle(@NotNull InventoryView view, @NotNull Component title) {
        ServerPlayer handle = (ServerPlayer) craftPlayer_getHandle.invoke(view.getPlayer());
        AbstractContainerMenu containerMenu = handle.containerMenu;

        int containerId = containerMenu.containerId;
        MenuType<?> type = containerMenu.getType();
        AdventureComponent minecraftTitle = new AdventureComponent(title);

        ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, type, minecraftTitle);

        handle.connection.send(packet);
        containerMenu.sendAllDataToRemote();

        abstractContainerMenu_setTitle.invoke(containerMenu, minecraftTitle);

        String rawTitle = PlainTextComponentSerializer.plainText().serialize(title);
        craftInventoryView_setTitle.invoke(view, rawTitle);
    }
}

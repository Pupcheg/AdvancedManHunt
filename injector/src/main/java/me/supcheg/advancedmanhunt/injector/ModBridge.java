package me.supcheg.advancedmanhunt.injector;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import me.supcheg.bridge.Bridge;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftContainer;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class ModBridge implements Bridge {
    @Override
    public void registerBrigadierCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        @SuppressWarnings("unchecked")
        LiteralArgumentBuilder<CommandSourceStack> casted = (LiteralArgumentBuilder<CommandSourceStack>) (Object) command;

        ((CraftServer) Bukkit.getServer()).getServer()
                .vanillaCommandDispatcher
                .getDispatcher()
                .register(casted);
    }

    @Override
    public void sendTitle(@NotNull InventoryView inventoryView, @NotNull Component title) {
        CraftPlayer player = (CraftPlayer) inventoryView.getPlayer();
        ServerPlayer handle = player.getHandle();

        int containerId = handle.containerMenu.containerId;
        MenuType<?> type = CraftContainer.getNotchInventoryType(inventoryView.getTopInventory());

        ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, type, new AdventureComponent(title));

        handle.connection.send(packet);
        handle.containerMenu.sendAllDataToRemote();
    }
}

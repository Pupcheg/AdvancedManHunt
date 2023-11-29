package me.supcheg.advancedmanhunt.injector;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.google.common.io.MoreFiles;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import me.supcheg.bridge.Bridge;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftContainer;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Path;

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

    @Override
    public void writePositionsToRegion(@NotNull Path regionPath) {
        String filename = MoreFiles.getNameWithoutExtension(regionPath);
        int dotIndex = filename.lastIndexOf('.');

        int firstX = Integer.parseInt(filename.substring(2, dotIndex)) << 5;
        int firstZ = Integer.parseInt(filename.substring(dotIndex + 1)) << 5;

        try (RegionFile regionFile = new RegionFile(regionPath, regionPath.getParent(), true, false)) {
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    ChunkPos chunkPos = new ChunkPos(x, z);

                    CompoundTag nbt;
                    try (DataInputStream in = regionFile.getChunkDataInputStream(chunkPos)) {
                        if (in == null) {
                            continue;
                        }
                        nbt = NbtIo.read(in);
                    }

                    nbt.putIntArray("Position", new int[]{firstX + x, firstZ + z});

                    try (DataOutputStream out = regionFile.getChunkDataOutputStream(chunkPos)) {
                        NbtIo.write(nbt, out);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

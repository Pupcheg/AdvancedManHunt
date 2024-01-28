package me.supcheg.advancedmanhunt.injector.bridge;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.util.SneakyThrow;
import com.google.common.io.MoreFiles;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import me.supcheg.bridge.Bridge;
import me.supcheg.bridge.item.ItemStackWrapperFactory;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Path;

import static me.supcheg.advancedmanhunt.injector.bridge.ReflectiveAccessor.craftContainer_getNotchInventoryType;
import static me.supcheg.advancedmanhunt.injector.bridge.ReflectiveAccessor.craftPlayer_getHandle;

public class ModBridge implements Bridge {

    @Override
    public void registerBrigadierCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        @SuppressWarnings("unchecked")
        LiteralArgumentBuilder<CommandSourceStack> casted = (LiteralArgumentBuilder<CommandSourceStack>) (Object) command;

        DedicatedServer.getServer()
                .vanillaCommandDispatcher
                .getDispatcher()
                .register(casted);
    }

    @Override
    public void sendTitle(@NotNull InventoryView inventoryView, @NotNull Component title) {
        try {
            ServerPlayer handle = (ServerPlayer) craftPlayer_getHandle.get().invoke(inventoryView.getPlayer());

            int containerId = handle.containerMenu.containerId;
            MenuType<?> type = (MenuType<?>) craftContainer_getNotchInventoryType.get().invoke(inventoryView.getTopInventory());

            ClientboundOpenScreenPacket packet = new ClientboundOpenScreenPacket(containerId, type, new AdventureComponent(title));

            handle.connection.send(packet);
            handle.containerMenu.sendAllDataToRemote();
        } catch (Throwable thr) {
            SneakyThrow.sneaky(thr);
        }
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
            SneakyThrow.sneaky(e);
        }
    }

    @NotNull
    @Override
    public ItemStackWrapperFactory getItemStackWrapperFactory() {
        return new NmsItemStackWrapperFactory();
    }
}

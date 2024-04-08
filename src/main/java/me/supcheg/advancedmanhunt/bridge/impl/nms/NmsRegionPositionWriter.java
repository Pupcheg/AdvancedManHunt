package me.supcheg.advancedmanhunt.bridge.impl.nms;

import com.google.common.io.MoreFiles;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.RegionPositionWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.file.Path;

public class NmsRegionPositionWriter implements RegionPositionWriter {
    @SneakyThrows
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
        }
    }
}

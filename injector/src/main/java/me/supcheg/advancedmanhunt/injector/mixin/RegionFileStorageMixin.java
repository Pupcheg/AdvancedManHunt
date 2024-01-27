package me.supcheg.advancedmanhunt.injector.mixin;

import me.supcheg.advancedmanhunt.injector.ChunkNbtFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Mixin(RegionFileStorage.class)
public class RegionFileStorageMixin {
    @Shadow
    private static void printOversizedLog(String msg, Path file, int x, int z) {
    }

    @SuppressWarnings("SameReturnValue") // shadowing
    @Shadow
    private static CompoundTag readOversizedChunk(RegionFile regionfile, ChunkPos chunkCoordinate) {
        return null;
    }

    /**
     * @author Supcheg
     * @reason Fix coordinates
     */
    @Overwrite
    public CompoundTag read(ChunkPos pos, RegionFile regionfile) throws IOException {
        try (DataInputStream datainputstream = regionfile.getChunkDataInputStream(pos)) {
            if (((RegionFileAccessor) regionfile).invokeIsOversized(pos.x, pos.z)) {
                printOversizedLog("Loading Oversized Chunk!", regionfile.regionFile, pos.x, pos.z);
                return readOversizedChunk(regionfile, pos);
            }

            if (datainputstream == null) {
                return null;
            }

            CompoundTag nbt = NbtIo.read(datainputstream);
            ChunkPos serializedPos = ChunkNbtFixer.getChunkPosition(nbt, pos);
            if (!serializedPos.equals(pos)) {
                ChunkNbtFixer.fixChunk(nbt, pos, serializedPos);
            }
            return nbt;
        } finally {
            regionfile.fileLock.unlock();
        }
    }
}

package me.supcheg.advancedmanhunt.injector.mixin;

import me.supcheg.advancedmanhunt.injector.ChunkNbtFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileAccessor;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Mixin(RegionFileStorage.class)
public class RegionFileStorageMixin {
    @Shadow
    @Final
    private boolean isChunkData;

    @Shadow
    private static void printOversizedLog(String msg, Path file, int x, int z) {
    }

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
            if (RegionFileAccessor.isOversized(regionfile, pos.x, pos.z)) {
                printOversizedLog("Loading Oversized Chunk!", regionfile.regionFile, pos.x, pos.z);
                return readOversizedChunk(regionfile, pos);
            }

            if (datainputstream == null) {
                return null;
            }

            CompoundTag nbt = NbtIo.read(datainputstream);
            if (this.isChunkData) {
                ChunkPos chunkPos = ChunkSerializer.getChunkCoordinate(nbt);
                if (!chunkPos.equals(pos)) {
                    ChunkNbtFixer.fixChunk(nbt, pos);
                }
            }
            return nbt;
        } finally {
            regionfile.fileLock.unlock();
        }
    }
}

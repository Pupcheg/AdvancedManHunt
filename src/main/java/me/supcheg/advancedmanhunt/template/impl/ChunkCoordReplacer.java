package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.CoordUtil;
import net.querz.mca.CompressionType;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public class ChunkCoordReplacer implements Closeable {
    private static final CompressionType OUT_COMPRESSION_TYPE = CompressionType.ZLIB;
    private static final CompressionType[] COMPRESSION_TYPES = CompressionType.values();

    private final RandomAccessFile sourceFile;
    private final RandomAccessFile targetFile;

    public ChunkCoordReplacer(@NotNull Path sourceFile, @NotNull Path targetFile) throws IOException {
        this.sourceFile = new RandomAccessFile(sourceFile.toFile(), "r");
        this.targetFile = new RandomAccessFile(targetFile.toString(), "rw");
    }

    public void copyRegion(int regionX, int regionZ) throws IOException {
        int globalOffset = 2;
        int lastWritten = 0;
        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        int chunkXOffset = CoordUtil.FIRST_CHUNK_FROM_REGION.applyAsInt(regionX);
        int chunkZOffset = CoordUtil.FIRST_CHUNK_FROM_REGION.applyAsInt(regionZ);

        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                int index = getChunkIndex(chunkX, chunkZ);
                CompoundTag chunk = readChunk(index);
                if (chunk == null) {
                    continue;
                }
                replaceAbsoluteCoords(chunk, chunkX + chunkXOffset, chunkZ + chunkZOffset);

                targetFile.seek(4096L * globalOffset);
                lastWritten = serializeChunk(chunk);

                if (lastWritten == 0) {
                    continue;
                }

                int sectors = (lastWritten >> 12) + (lastWritten % 4096 == 0 ? 0 : 1);

                targetFile.seek(index * 4L);
                targetFile.writeByte(globalOffset >>> 16);
                targetFile.writeByte(globalOffset >> 8 & 0xFF);
                targetFile.writeByte(globalOffset & 0xFF);
                targetFile.writeByte(sectors);

                // write timestamp
                targetFile.seek(index * 4L + 4096);
                targetFile.writeInt(timestamp);

                globalOffset += sectors;
            }
        }

        // padding
        if (lastWritten % 4096 != 0) {
            targetFile.seek(globalOffset * 4096L - 1);
            targetFile.write(0);
        }
    }

    private static int getChunkIndex(int chunkX, int chunkZ) {
        return (chunkX & 0x1F) + (chunkZ & 0x1F) * 32;
    }

    private static void replaceAbsoluteCoords(@NotNull CompoundTag tag, int x, int z) {
        tag.put("xPos", new IntTag(x));
        tag.put("zPos", new IntTag(z));
    }

    private int serializeChunk(@NotNull CompoundTag chunk) throws IOException {
        byte[] rawData = serialize(chunk);
        targetFile.writeInt(rawData.length + 1); // including the byte to store the compression type
        targetFile.writeByte(OUT_COMPRESSION_TYPE.ordinal());
        targetFile.write(rawData);
        return rawData.length + 5;
    }

    private static byte @NotNull [] serialize(@NotNull CompoundTag tag) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        try (NBTOutputStream nbtOut = new NBTOutputStream(new BufferedOutputStream(OUT_COMPRESSION_TYPE.compress(out)))) {
            nbtOut.writeTag(tag, Tag.DEFAULT_MAX_DEPTH);
        }
        return out.toByteArray();
    }

    @Nullable
    private CompoundTag readChunk(long index) throws IOException {
        sourceFile.seek(index * 4);
        int offset = sourceFile.read() << 16;
        offset |= (sourceFile.read() & 0xFF) << 8;
        offset |= sourceFile.read() & 0xFF;
        if (sourceFile.readByte() == 0) {
            return null;
        }
        sourceFile.seek(4096L * offset + 4); //+4: skip data size
        return deserializeCurrentChunk();
    }

    @NotNull
    private CompoundTag deserializeCurrentChunk() throws IOException {
        int compressionTypeIndex = sourceFile.read();
        CompressionType compressionType = COMPRESSION_TYPES[compressionTypeIndex];

        NamedTag tag = openNbtInputStream(compressionType).readTag(Tag.DEFAULT_MAX_DEPTH);

        if (tag != null && tag.getTag() instanceof CompoundTag compoundTag) {
            return compoundTag;
        }

        throw new IOException("invalid data tag: " + (tag == null ? "null" : tag.getClass().getName()));
    }

    @NotNull
    private NBTInputStream openNbtInputStream(@NotNull CompressionType compressionType) throws IOException {
        return new NBTInputStream(new BufferedInputStream(compressionType.decompress(new FileInputStream(sourceFile.getFD()))));
    }

    @Override
    public void close() throws IOException {
        sourceFile.close();
        targetFile.close();
    }
}

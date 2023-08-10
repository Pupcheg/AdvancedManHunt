package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.CoordUtil;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class ChunkCoordReplacer implements Closeable {
    private final RandomAccessFile file;

    public ChunkCoordReplacer(@NotNull Path path) throws IOException {
        this.file = new RandomAccessFile(path.toFile(), "rw");
    }

    public void replace(int regionX, int regionZ) throws IOException {
        int chunkXOffset = CoordUtil.getFirstChunkInRegion(regionX);
        int chunkZOffset = CoordUtil.getFirstChunkInRegion(regionZ);

        for (int chunkX = 0; chunkX < 32; chunkX++) {
            for (int chunkZ = 0; chunkZ < 32; chunkZ++) {
                int offset = readOffset(chunkX, chunkZ);

                if (offset == -1) {
                    continue;
                }

                CompoundTag chunk = readChunk(offset);
                replaceAbsoluteCoords(chunk, chunkX + chunkXOffset, chunkZ + chunkZOffset);
                writeChunk(offset, chunk);
            }
        }
    }

    private int readOffset(int chunkX, int chunkZ) throws IOException {
        long chunkIndex = (chunkX & 31) + (chunkZ & 31) * 32;

        file.seek(chunkIndex * 4);

        int offset = file.readByte() << 16;
        offset |= (file.readByte() & 0xFF) << 8;
        offset |= file.readByte() & 0xFF;

        if (file.readByte() == 0) { // sectors count
            return -1;
        }
        return offset;
    }

    @NotNull
    private CompoundTag readChunk(int offset) throws IOException {
        moveCursorToOffset(offset);
        return (CompoundTag) openNbtInputStream().readTag(Tag.DEFAULT_MAX_DEPTH).getTag();
    }

    private void moveCursorToOffset(int offset) throws IOException {
        file.seek(4096L * offset + 4 + 1); // skip size (4) and compression type (1)
    }

    @NotNull
    private NBTInputStream openNbtInputStream() throws IOException {
        return new NBTInputStream(new BufferedInputStream(new InflaterInputStream(new FileInputStream(file.getFD()))));
    }

    private static void replaceAbsoluteCoords(@NotNull CompoundTag tag, int x, int z) {
        tag.putInt("xPos", x);
        tag.putInt("zPos", z);
    }

    private void writeChunk(int offset, @NotNull CompoundTag chunk) throws IOException {
        moveCursorToOffset(offset);
        file.write(toCompressedBytes(chunk));
    }

    private static byte @NotNull [] toCompressedBytes(@NotNull Tag<?> tag) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(11264);
        try (NBTOutputStream out = new NBTOutputStream(new DeflaterOutputStream(byteArrayOutputStream))) {
            out.writeTag(tag, Tag.DEFAULT_MAX_DEPTH);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}

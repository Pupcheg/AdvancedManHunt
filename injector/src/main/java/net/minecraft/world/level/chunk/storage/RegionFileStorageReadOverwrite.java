package net.minecraft.world.level.chunk.storage;

import lombok.SneakyThrows;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Objects;

@SuppressWarnings("unused")
public class RegionFileStorageReadOverwrite {

    @Nullable
    public static CompoundTag read(Object storage, ChunkPos pos, RegionFile regionfile) throws Throwable {
        try (DataInputStream datainputstream = regionfile.getChunkDataInputStream(pos)) {
            if (regionfile.isOversized(pos.x, pos.z)) {
                printOversizedLog(storage, "Loading Oversized Chunk!", regionfile.regionFile, pos);
                return readOversizedChunk(storage, regionfile, pos);
            }

            if (datainputstream == null) {
                return null;
            }

            CompoundTag nbt = NbtIo.read(datainputstream);
            ChunkPos serializedPos = getChunkPosition(nbt, pos);
            if (!serializedPos.equals(pos)) {
                fixChunk(nbt, pos, serializedPos);
            }
            return nbt;
        } finally {
            regionfile.fileLock.unlock();
        }
    }

    public static final MethodHandle regionFileStorage_printOversizedLog =
            resolve(RegionFileStorage.class, "printOversizedLog", String.class, Path.class, int.class, int.class);
    public static final MethodHandle regionFileStorage_readOversizedChunk =
            resolve(RegionFileStorage.class, "readOversizedChunk", RegionFile.class, ChunkPos.class);

    public static void printOversizedLog(Object storage, String msg, Path path, ChunkPos pos) throws Throwable {
        regionFileStorage_printOversizedLog.invoke(storage, msg, path, pos.x, pos.z);
    }

    public static CompoundTag readOversizedChunk(Object storage, RegionFile regionfile, ChunkPos pos) throws Throwable {
        return (CompoundTag) regionFileStorage_readOversizedChunk.invoke(storage, regionfile, pos);
    }

    @SneakyThrows
    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static MethodHandle resolve(@NotNull Class<?> clazz, @NotNull String methodName, @NotNull Class<?>... parameters) {
        Method method = clazz.getDeclaredMethod(methodName, parameters);
        method.trySetAccessible();
        return MethodHandles.lookup().unreflect(method);
    }


    private static final String BLOCK_ENTITIES = "block_entities";
    private static final String STRUCTURES = "structures";
    private static final String ENTITIES = "Entities";
    private static final String SECTIONS = "Sections";

    private static final String POSITION = "Position";
    private static final String X_POS = "xPos";
    private static final String Z_POS = "zPos";

    private static final String X = "x";
    private static final String Z = "z";

    private static final String STARTS = "starts";
    private static final String CHUNK_X = "ChunkX";
    private static final String CHUNK_Z = "ChunkZ";
    private static final String REFERENCES = "References";

    private static final String POS_UPPER = "Pos";
    private static final String PAPER_ORIGIN = "Paper.Origin";

    private static final String RECORDS = "Records";
    private static final String POS_LOWER = "pos";

    @NotNull
    public static ChunkPos getChunkPosition(@NotNull CompoundTag nbt, @NotNull ChunkPos pos) {
        int x;
        int z;

        Tag position = nbt.get(POSITION);
        if (position != null) {
            int[] positionArray = ((IntArrayTag) position).getAsIntArray();
            x = positionArray[0];
            z = positionArray[1];
        } else {
            Tag xPos = nbt.get(X_POS);
            if (xPos == null) {
                return pos;
            }
            x = ((NumericTag) xPos).getAsInt();
            z = nbt.getInt(Z_POS);
        }
        return new ChunkPos(x, z);
    }

    public static void fixChunk(@NotNull CompoundTag nbt, @NotNull ChunkPos required, @NotNull ChunkPos original) {
        putPosition(nbt, required);

        Tag blockEntities = nbt.get(BLOCK_ENTITIES);
        if (blockEntities != null) {
            fixBlockEntities((ListTag) blockEntities, required, original);
        }

        Tag structures = nbt.get(STRUCTURES);
        if (structures != null) {
            fixStructures((CompoundTag) structures, required, original);
        }

        Tag entities = nbt.get(ENTITIES);
        if (entities != null) {
            fixEntities((ListTag) entities, required, original);
        }

        Tag sections = nbt.get(SECTIONS);
        if (sections != null) {
            fixSections((CompoundTag) sections, required, original);
        }
    }

    private static void putPosition(@NotNull CompoundTag nbt, @NotNull ChunkPos required) {
        Tag position = nbt.get(POSITION);
        if (position != null) {
            int[] positionArray = ((IntArrayTag) position).getAsIntArray();
            positionArray[0] = required.x;
            positionArray[1] = required.z;
        } else {
            nbt.putInt(X_POS, required.x);
            nbt.putInt(Z_POS, required.z);
        }
    }

    private static void fixBlockEntities(@NotNull ListTag nbt, @NotNull ChunkPos required, @NotNull ChunkPos original) {
        for (Tag blockEntity : nbt) {
            CompoundTag blockEntityCompound = (CompoundTag) blockEntity;

            int x = blockEntityCompound.getInt(X) + (required.x - original.x) * 16;
            int z = blockEntityCompound.getInt(Z) + (required.z - original.z) * 16;

            blockEntityCompound.putInt(X, x);
            blockEntityCompound.putInt(Z, z);
        }
    }

    private static void fixStructures(@NotNull CompoundTag nbt, @NotNull ChunkPos required, @NotNull ChunkPos original) {
        for (Tag start : nbt.getCompound(STARTS).tags.values()) {
            CompoundTag startCompound = (CompoundTag) start;
            startCompound.putInt(CHUNK_X, required.x);
            startCompound.putInt(CHUNK_Z, required.z);
        }

        for (Tag reference : nbt.getCompound(REFERENCES).tags.values()) {
            long[] referenceArray = ((LongArrayTag) reference).getAsLongArray();
            for (int i = 0; i < referenceArray.length; i++) {
                long referenceKey = referenceArray[i];

                int expectedX = original.x - ChunkPos.getX(referenceKey) + required.x;
                int expectedZ = original.z - ChunkPos.getZ(referenceKey) + required.z;

                referenceArray[i] = ChunkPos.asLong(expectedX, expectedZ);
            }
        }
    }

    private static void fixEntities(@NotNull ListTag nbt, @NotNull ChunkPos required, @NotNull ChunkPos original) {
        for (Tag entity : nbt) {
            CompoundTag entityCompound = (CompoundTag) entity;
            fixCoords(required, original, entityCompound, POS_UPPER);
            fixCoords(required, original, entityCompound, PAPER_ORIGIN);
        }
    }

    private static void fixCoords(@NotNull ChunkPos required,
                                  @NotNull ChunkPos original,
                                  @NotNull CompoundTag entityCompound,
                                  @NotNull String key) {
        ListTag tag = (ListTag) entityCompound.get(key);
        if (tag != null) {
            double x = ((NumericTag) tag.get(0)).getAsDouble();
            x += (required.x - original.x) * 16;
            tag.set(0, DoubleTag.valueOf(x));

            double z = ((NumericTag) tag.get(2)).getAsDouble();
            z += (required.z - original.z) * 16;
            tag.set(2, DoubleTag.valueOf(z));
        }
    }

    private static void fixSections(@NotNull CompoundTag nbt, @NotNull ChunkPos required, @NotNull ChunkPos original) {
        for (Tag section : nbt.tags.values()) {
            CompoundTag sectionCompound = (CompoundTag) section;

            ListTag recordList = (ListTag) sectionCompound.get(RECORDS);
            Objects.requireNonNull(recordList, RECORDS);

            for (Tag record : recordList) {
                CompoundTag recordCompound = (CompoundTag) record;
                IntArrayTag intArray = (IntArrayTag) recordCompound.get(POS_LOWER);
                Objects.requireNonNull(intArray, POS_LOWER);

                int[] posArray = intArray.getAsIntArray();
                posArray[0] += (required.x - original.x) * 16;
                posArray[1] = (required.z - original.z) * 16;
            }
        }
    }
}
package me.supcheg.advancedmanhunt.injector;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

public class ChunkNbtFixer {
    private static final String X_POS = "xPos";
    private static final String Z_POS = "zPos";

    private static final String X = "x";
    private static final String Z = "z";

    private static final String CHUNK_X = "ChunkX";
    private static final String CHUNK_Z = "ChunkZ";

    private static final String BLOCK_ENTITIES = "block_entities";
    private static final String STRUCTURES = "structures";
    private static final String ENTITIES = "Entities";

    private static final String STARTS = "starts";
    private static final String REFERENCES = "References";
    private static final String POS = "Pos";
    private static final String POSITION = "Position";
    private static final String PAPER_ORIGIN = "Paper.Origin";

    private ChunkNbtFixer() {
    }

    public static void fixChunk(@NotNull CompoundTag nbt, @NotNull ChunkPos pos) {
        ChunkPos originalPos;

        Tag position = nbt.get(POSITION);
        if (position != null) {
            int[] positionArray = ((IntArrayTag) position).getAsIntArray();
            originalPos = new ChunkPos(positionArray[0], positionArray[1]);

            positionArray[0] = pos.x;
            positionArray[1] = pos.z;
        } else {
            originalPos = new ChunkPos(nbt.getInt(X_POS), nbt.getInt(Z_POS));
            nbt.putInt(X_POS, pos.x);
            nbt.putInt(Z_POS, pos.z);
        }

        Tag blockEntities = nbt.get(BLOCK_ENTITIES);
        if (blockEntities != null) {
            fixBlockEntities((ListTag) blockEntities, pos, originalPos);
        }

        Tag structures = nbt.get(STRUCTURES);
        if (structures != null) {
            fixStructures((CompoundTag) structures, pos, originalPos);
        }

        Tag entities = nbt.get(ENTITIES);
        if (entities != null) {
            fixEntities((ListTag) entities, pos, originalPos);
        }
    }

    private static void fixBlockEntities(@NotNull ListTag nbt, @NotNull ChunkPos pos, @NotNull ChunkPos original) {
        for (Tag blockEntity : nbt) {
            CompoundTag blockEntityCompound = (CompoundTag) blockEntity;

            int x = blockEntityCompound.getInt(X) + (pos.x - original.x) * 16;
            int z = blockEntityCompound.getInt(Z) + (pos.z - original.z) * 16;

            blockEntityCompound.putInt(X, x);
            blockEntityCompound.putInt(Z, z);
        }
    }

    private static void fixStructures(@NotNull CompoundTag nbt, @NotNull ChunkPos pos, @NotNull ChunkPos original) {
        for (Tag start : nbt.getCompound(STARTS).tags.values()) {
            CompoundTag startCompound = (CompoundTag) start;
            startCompound.putInt(CHUNK_X, pos.x);
            startCompound.putInt(CHUNK_Z, pos.z);
        }

        for (Tag reference : nbt.getCompound(REFERENCES).tags.values()) {
            long[] referenceArray = ((LongArrayTag) reference).getAsLongArray();
            for (int i = 0; i < referenceArray.length; i++) {
                long referenceKey = referenceArray[i];

                int expectedX = original.x - ChunkPos.getX(referenceKey) + pos.x;
                int expectedZ = original.z - ChunkPos.getZ(referenceKey) + pos.z;

                referenceArray[i] = ChunkPos.asLong(expectedX, expectedZ);
            }
        }
    }

    private static void fixEntities(@NotNull ListTag nbt, @NotNull ChunkPos pos, @NotNull ChunkPos original) {
        for (Tag entity : nbt) {
            CompoundTag entityCompound = (CompoundTag) entity;
            fixCoords(pos, original, entityCompound, POS);
            fixCoords(pos, original, entityCompound, PAPER_ORIGIN);
        }
    }

    private static void fixCoords(@NotNull ChunkPos pos,
                                  @NotNull ChunkPos original,
                                  @NotNull CompoundTag entityCompound,
                                  @NotNull String key) {
        ListTag tag = (ListTag) entityCompound.get(key);
        if (tag != null) {
            double x = ((NumericTag) tag.get(0)).getAsDouble();
            x += (pos.x - original.x) * 16;
            tag.set(0, DoubleTag.valueOf(x));

            double z = ((NumericTag) tag.get(2)).getAsDouble();
            z += (pos.z - original.z) * 16;
            tag.set(2, DoubleTag.valueOf(z));
        }
    }
}

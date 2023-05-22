package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CoordUtil {
    public static final IntUnaryOperator CHUNK_FROM_BLOCK = i -> i >> 4;
    public static final IntUnaryOperator REGION_FROM_CHUNK = i -> i >> 5;
    public static final IntUnaryOperator FIRST_BLOCK_FROM_CHUNK = i -> i << 4;
    public static final IntUnaryOperator LAST_BLOCK_FROM_CHUNK = FIRST_BLOCK_FROM_CHUNK.andThen(i -> i + 15);
    public static final IntUnaryOperator FIRST_CHUNK_FROM_REGION = i -> i << 5;
    public static final IntUnaryOperator LAST_CHUNK_FROM_REGION = FIRST_CHUNK_FROM_REGION.andThen(i -> i + 31);

    public static int getX(long key) {
        return (int) key;
    }

    public static int getZ(long key) {
        return (int) (key >> 32);
    }

    public static long getKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getChunkFromBlock(@NotNull KeyedCoord blockCoord) {
        return blockCoord.map(CHUNK_FROM_BLOCK);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getRegionFromChunk(@NotNull KeyedCoord chunkCoord) {
        return chunkCoord.map(REGION_FROM_CHUNK);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getFirstBlockInChunk(@NotNull KeyedCoord chunkCoord) {
        return chunkCoord.map(FIRST_BLOCK_FROM_CHUNK);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getLastBlockInChunk(@NotNull KeyedCoord chunkCoord) {
        return chunkCoord.map(LAST_BLOCK_FROM_CHUNK);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getFirstChunkInRegion(@NotNull KeyedCoord regionCoord) {
        return regionCoord.map(FIRST_CHUNK_FROM_REGION);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getLastChunkInRegion(@NotNull KeyedCoord regionCoord) {
        return regionCoord.map(LAST_CHUNK_FROM_REGION);
    }

    @Contract(pure = true)
    public static boolean isInBoundInclusive(@NotNull KeyedCoord coord, @NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return coord.getX() >= start.getX() && coord.getX() <= end.getX()
                && coord.getZ() >= start.getZ() && coord.getZ() <= end.getZ();
    }

    @Contract(pure = true)
    public static boolean isInBoundExclusive(@NotNull KeyedCoord coord, @NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return coord.getX() > start.getX() && coord.getX() < end.getX()
                && coord.getZ() > start.getZ() && coord.getZ() < end.getZ();
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Stream<KeyedCoord> streamInclusive(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterateInclusive(start, end), Spliterator.ORDERED), false);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static CoordIterator iterateInclusive(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return new CoordIterator(start, end);
    }

    @Contract(pure = true)
    public static void checkBound(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        if (start.getX() > end.getX() || start.getZ() > end.getZ()) {
            throw new IllegalArgumentException("Bad bound: start: %s, end: %s".formatted(start, end));
        }
    }
}

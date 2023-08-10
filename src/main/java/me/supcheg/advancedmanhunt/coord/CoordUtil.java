package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CoordUtil {
    public static int getX(long key) {
        return (int) key;
    }

    public static int getZ(long key) {
        return (int) (key >> 32);
    }

    public static long getKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    public static int getChunkFromBlock(int blockCoord) {
        return blockCoord >> 4;
    }

    public static int getRegionFromChunk(int chunkCoord) {
        return chunkCoord >> 5;
    }

    public static int getFirstBlockInChunk(int chunkCoord) {
        return chunkCoord << 4;
    }

    public static int getLastBlockInChunk(int chunkCoord) {
        return (chunkCoord << 4) + 15;
    }

    public static int getFirstChunkInRegion(int regionCoord) {
        return regionCoord << 5;
    }

    public static int getLastChunkInRegion(int regionCoord) {
        return (regionCoord << 5) + 31;
    }

    public static boolean isInBoundInclusive(@NotNull KeyedCoord coord, @NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return coord.getX() >= start.getX() && coord.getX() <= end.getX()
                && coord.getZ() >= start.getZ() && coord.getZ() <= end.getZ();
    }

    public static boolean isInBoundExclusive(@NotNull KeyedCoord coord, @NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return coord.getX() > start.getX() && coord.getX() < end.getX()
                && coord.getZ() > start.getZ() && coord.getZ() < end.getZ();
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Stream<KeyedCoord> streamInclusive(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        CoordIterator it = iterateInclusive(start, end);
        return StreamSupport.stream(
                Spliterators.spliterator(it, it.allCount(), Spliterator.ORDERED | Spliterator.SIZED |
                        Spliterator.SORTED | Spliterator.NONNULL),
                false);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static CoordIterator iterateInclusive(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        checkBound(start, end);
        return new CoordIterator(start, end);
    }

    public static void checkBound(@NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        if (start.getX() > end.getX() || start.getZ() > end.getZ()) {
            throw new IllegalArgumentException("Bad bound: start: %s, end: %s".formatted(start, end));
        }
    }
}

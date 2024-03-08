package me.supcheg.advancedmanhunt.coord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Coords {

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

    public static boolean isInBoundInclusive(@NotNull Coord coord, @NotNull Coord start, @NotNull Coord end) {
        checkBound(start, end);
        return coord.getX() >= start.getX() && coord.getX() <= end.getX()
                && coord.getZ() >= start.getZ() && coord.getZ() <= end.getZ();
    }

    public static boolean isInBoundExclusive(@NotNull Coord coord, @NotNull Coord start, @NotNull Coord end) {
        checkBound(start, end);
        return coord.getX() > start.getX() && coord.getX() < end.getX()
                && coord.getZ() > start.getZ() && coord.getZ() < end.getZ();
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static CoordRangeIterator iterateRangeInclusive(@NotNull Coord start, @NotNull Coord end) {
        checkBound(start, end);
        return new CoordRangeIterator(start, end);
    }

    public static void checkBound(@NotNull Coord start, @NotNull Coord end) {
        if (start.getX() > end.getX() || start.getZ() > end.getZ()) {
            throw new IllegalArgumentException("Bad bound: start: %s, end: %s".formatted(start, end));
        }
    }
}

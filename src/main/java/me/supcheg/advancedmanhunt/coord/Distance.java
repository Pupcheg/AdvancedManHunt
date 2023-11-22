package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("LombokGetterMayBeUsed")
public class Distance implements Comparable<Distance> {

    private static final int CHUNKS = 16;
    private static final int REGIONS = CHUNKS * 32;

    private final int blocks;

    private Distance(int blocks) {
        if (blocks < 0) {
            throw new IllegalArgumentException("Distance (" + blocks + ") can't be negative");
        }
        this.blocks = blocks;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Distance ofRegions(int value) {
        return new Distance(value * REGIONS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Distance ofChunks(int value) {
        return new Distance(value * CHUNKS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Distance ofBlocks(int value) {
        return new Distance(value);
    }

    public int getRegions() {
        return Math.floorDiv(blocks, REGIONS);
    }

    public int getChunks() {
        return Math.floorDiv(blocks, CHUNKS);
    }

    public int getBlocks() {
        return blocks;
    }

    public boolean isFullRegions() {
        return blocks % REGIONS == 0;
    }

    public boolean isFullChunks() {
        return blocks % CHUNKS == 0;
    }

    public double getExactRegions() {
        return blocks / (double) REGIONS;
    }

    public double getExactChunks() {
        return blocks / (double) CHUNKS;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance addBlocks(int value) {
        return ofBlocks(blocks + value);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance addChunks(int value) {
        return ofBlocks(blocks + value * CHUNKS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance addRegions(int value) {
        return ofBlocks(blocks + value * REGIONS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance add(@NotNull Distance distance) {
        return ofBlocks(blocks + distance.blocks);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance subtractBlocks(int value) {
        return ofBlocks(blocks - value);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance subtractChunks(int value) {
        return ofBlocks(blocks - value * CHUNKS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance subtractRegions(int value) {
        return ofBlocks(blocks - value * REGIONS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Distance subtract(@NotNull Distance distance) {
        return ofBlocks(blocks - distance.blocks);
    }

    public boolean isLessThan(@NotNull Distance other) {
        return blocks < other.blocks;
    }

    public boolean isGreaterThan(@NotNull Distance other) {
        return blocks > other.blocks;
    }

    public boolean isEquals(@NotNull Distance other) {
        return blocks == other.blocks;
    }

    public boolean isEqualsOrLessThan(@NotNull Distance other) {
        return blocks <= other.blocks;
    }

    public boolean isEqualsOrGreaterThan(@NotNull Distance other) {
        return blocks >= other.blocks;
    }

    @Override
    public int compareTo(@NotNull Distance other) {
        return Integer.compare(blocks, other.blocks);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Distance distance)) {
            return false;
        }

        return blocks == distance.blocks;
    }

    @Override
    public int hashCode() {
        return blocks;
    }

    @NotNull
    @Override
    public String toString() {
        return String.valueOf(blocks);
    }
}

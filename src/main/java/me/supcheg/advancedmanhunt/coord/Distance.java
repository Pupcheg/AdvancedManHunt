package me.supcheg.advancedmanhunt.coord;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
public class Distance {

    private static final int CHUNKS = 16;
    private static final int REGIONS = CHUNKS * 32;

    private final int blocks;

    private Distance(int blocks) {
        this.blocks = blocks;
    }

    @NotNull
    public static Distance ofRegions(int distance) {
        return new Distance(distance * REGIONS);
    }

    @NotNull
    public static Distance ofChunks(int distance) {
        return new Distance(distance * CHUNKS);
    }

    @NotNull
    public static Distance ofBlocks(int distance) {
        return new Distance(distance);
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
}

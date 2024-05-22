package me.supcheg.advancedmanhunt.math.distance;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.math.distance.Distance.CHUNKS;
import static me.supcheg.advancedmanhunt.math.distance.Distance.REGIONS;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DistancePair implements BlockPosition {

    private final int blockX;
    private final int blockZ;

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DistancePair of(@NotNull Distance x, @NotNull Distance z) {
        Objects.requireNonNull(x, "x");
        Objects.requireNonNull(z, "z");
        return new DistancePair(x.getBlocks(), z.getBlocks());
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DistancePair ofRegions(int x, int z) {
        return new DistancePair(x * REGIONS, z * REGIONS);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DistancePair ofChunks(int x, int z) {
        return new DistancePair(x * CHUNKS, z * CHUNKS);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static DistancePair ofBlocks(int x, int z) {
        return new DistancePair(x, z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DistancePair ofSame(@NotNull Distance xz) {
        Objects.requireNonNull(xz, "xz");
        return new DistancePair(xz.getBlocks(), xz.getBlocks());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DistancePair ofRegionsSame(int xz) {
        return ofRegions(xz, xz);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DistancePair ofChunksSame(int xz) {
        return ofChunks(xz, xz);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static DistancePair ofBlocksSame(int xz) {
        return ofBlocks(xz, xz);
    }

    public int getRegionX() {
        return Math.floorDiv(blockX, REGIONS);
    }

    public int getChunkX() {
        return Math.floorDiv(blockX, CHUNKS);
    }

    public int getRegionZ() {
        return Math.floorDiv(blockZ, REGIONS);
    }

    public int getChunkZ() {
        return Math.floorDiv(blockZ, CHUNKS);
    }

    public boolean isFullRegions() {
        return blockX % REGIONS == 0 && blockZ % REGIONS == 0;
    }

    public boolean isFullChunks() {
        return blockX % CHUNKS == 0 && blockZ % CHUNKS == 0;
    }

    public double getExactRegionX() {
        return blockX / (double) REGIONS;
    }

    public double getExactChunkX() {
        return blockX / (double) CHUNKS;
    }

    public double getExactRegionZ() {
        return blockZ / (double) REGIONS;
    }

    public double getExactChunkZ() {
        return blockZ / (double) CHUNKS;
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair addBlocks(int x, int z) {
        return ofBlocks(blockX + x, blockZ + z);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair addChunks(int x, int z) {
        return ofBlocks(blockX + x * CHUNKS, blockZ + z * CHUNKS);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair addRegions(int x, int z) {
        return ofBlocks(blockX + x * REGIONS, blockZ + z * CHUNKS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public DistancePair add(@NotNull DistancePair distance) {
        return ofBlocks(blockX + distance.blockX, blockZ + distance.blockZ);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair subtractBlocks(int x, int z) {
        return ofBlocks(blockX - x, blockX - z);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair subtractChunks(int x, int z) {
        return ofBlocks(blockX - x * CHUNKS, blockZ - z * CHUNKS);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public DistancePair subtractRegions(int x, int z) {
        return ofBlocks(blockX - x * REGIONS, blockZ - z * REGIONS);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public DistancePair subtract(@NotNull DistancePair distance) {
        return ofBlocks(blockX - distance.blockX, blockZ - distance.blockZ);
    }

    @NotNull
    public BlockPosition blockPos() {
        return this;
    }

    @NotNull
    public BlockPosition chunkPos() {
        return Position.block(getChunkX(), 0, getChunkZ());
    }

    @NotNull
    public BlockPosition regionPos() {
        return Position.block(getRegionX(), 0, getRegionZ());
    }

    @Override
    public int blockX() {
        return getBlockX();
    }

    @Override
    public int blockY() {
        return 0;
    }

    @Override
    public int blockZ() {
        return getBlockZ();
    }
}

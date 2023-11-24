package net.minecraft.world.level.chunk.storage;

public class RegionFileAccessor {
    private RegionFileAccessor() {
    }

    public static boolean isOversized(RegionFile regionFile, int x, int z) {
        return regionFile.isOversized(x, z);
    }
}

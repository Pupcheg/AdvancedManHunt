package me.supcheg.advancedmanhunt.region;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Coord;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.coord.Coords.iterableRangeInclusive;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameRegions {
    public static void unload(@NotNull GameRegion region) {
        World world = region.getWorld();
        for (Coord coord : iterableRangeInclusive(region.getStartChunk(), region.getEndChunk())) {
            boolean unloadResult = world.unloadChunk(coord.getX(), coord.getZ(), false);
            if (!unloadResult) {
                throw new IllegalStateException("Unable to unload chunk " + coord);
            }
        }
    }
}

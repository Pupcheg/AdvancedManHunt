package me.supcheg.advancedmanhunt.region;

import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameRegions {
    public static void unload(@NotNull GameRegion region) {
        World world = region.positionSource().world().getWorld();
        PositionBox box = PositionBox.box(region.start().chunkPos(), region.end().chunkPos());

        for (Position position : PositionBoxIteratorSources.XZ.iterable(box)) {
            boolean unloadResult = world.unloadChunk(position.blockX(), position.blockZ(), false);
            if (!unloadResult) {
                throw new IllegalStateException("Unable to unload chunk " + position);
            }
        }
    }
}

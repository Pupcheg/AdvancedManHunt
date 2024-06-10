package me.supcheg.advancedmanhunt.template.impl;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.PositionBox;
import me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.math.PositionBox.box;
import static me.supcheg.advancedmanhunt.math.Positions.sameXZ;

public final class BukkitWorldGenerator implements WorldGenerator {
    @Override
    public void generate(@NotNull World world, int radius, @NotNull Runnable afterGeneration) {
        PositionBox box = box(sameXZ(-radius / 8), sameXZ(radius / 8));
        for (Position pos : PositionBoxIteratorSources.XZ.iterable(box)) {
            world.loadChunk(pos.blockX(), pos.blockZ());
        }
        afterGeneration.run();
    }
}

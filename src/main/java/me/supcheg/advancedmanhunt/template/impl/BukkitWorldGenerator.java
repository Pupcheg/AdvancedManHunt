package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.coord.Coords.iterableRangeInclusive;

public class BukkitWorldGenerator implements WorldGenerator {
    @Override
    public void generate(@NotNull World world, int radius, @NotNull Runnable afterGeneration) {
        for (Coord coord : iterableRangeInclusive(Coord.coordSameXZ(-radius / 8), Coord.coordSameXZ(radius / 8))) {
            world.loadChunk(coord.getX(), coord.getZ());
        }
        afterGeneration.run();
    }
}

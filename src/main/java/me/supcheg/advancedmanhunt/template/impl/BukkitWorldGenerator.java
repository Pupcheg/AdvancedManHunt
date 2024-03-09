package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.coord.CoordRangeIterator;
import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class BukkitWorldGenerator implements WorldGenerator {
    @Override
    public void generate(@NotNull World world, int radius, @NotNull Runnable afterGeneration) {
        CoordRangeIterator it = Coords.iterateRangeInclusive(Coord.coordSameXZ(-radius / 8), Coord.coordSameXZ(radius / 8));
        for (; it.hasNext(); it.next()) {
            world.loadChunk(it.getX(), it.getZ());
        }
        afterGeneration.run();
    }
}

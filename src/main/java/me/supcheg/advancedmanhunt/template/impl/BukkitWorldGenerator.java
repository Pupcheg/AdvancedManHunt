package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.CoordIterator;
import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BukkitWorldGenerator implements WorldGenerator {
    @NotNull
    @Override
    public CompletableFuture<Void> generate(@NotNull World world, int radius) {
        return CompletableFuture.runAsync(() -> {
            CoordIterator it = CoordUtil.iterateInclusive(KeyedCoord.of(-radius / 8), KeyedCoord.of(radius / 8));
            for (; it.hasNext(); it.next()) {
                world.loadChunk(it.getX(), it.getZ());
            }
        });
    }
}

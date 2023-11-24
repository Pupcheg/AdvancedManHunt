package me.supcheg.advancedmanhunt.template;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public interface WorldGenerator {
    void generate(@NotNull World world, int radius, @NotNull Runnable afterGeneration);
}

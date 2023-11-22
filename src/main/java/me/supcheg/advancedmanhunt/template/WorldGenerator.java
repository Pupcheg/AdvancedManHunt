package me.supcheg.advancedmanhunt.template;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface WorldGenerator {
    @NotNull
    CompletableFuture<Void> generate(@NotNull World world, int radius);
}

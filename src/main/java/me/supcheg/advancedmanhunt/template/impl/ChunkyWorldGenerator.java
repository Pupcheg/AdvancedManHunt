package me.supcheg.advancedmanhunt.template.impl;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.popcraft.chunky.Chunky;
import org.popcraft.chunky.GenerationTask;
import org.popcraft.chunky.Selection;
import org.popcraft.chunky.platform.BukkitWorld;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class ChunkyWorldGenerator implements WorldGenerator {
    private final Chunky chunky;
    private final Executor executor;

    public ChunkyWorldGenerator() {
        RegisteredServiceProvider<Chunky> chunkyService = Bukkit.getServicesManager().getRegistration(Chunky.class);
        Objects.requireNonNull(chunkyService, "Chunky service");

        this.chunky = chunkyService.getProvider();
        Objects.requireNonNull(chunky, "chunky");

        this.executor = chunky.getScheduler()::runTask;
    }

    @NotNull
    @Override
    public CompletableFuture<Void> generate(@NotNull World world, int radius) {
        Selection selection = Selection.builder(chunky, new BukkitWorld(world))
                .center(0, 0)
                .radiusX(radius)
                .radiusZ(radius)
                .build();

        GenerationTask generationTask = new GenerationTask(chunky, selection);
        chunky.getGenerationTasks().put(world.getName(), generationTask);

        return CompletableFuture.runAsync(generationTask, executor);
    }
}

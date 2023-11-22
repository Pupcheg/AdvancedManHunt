package me.supcheg.advancedmanhunt.region.impl;

import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import me.supcheg.advancedmanhunt.util.ThreadSafeRandom;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CachedSpawnLocationFinder implements SpawnLocationFinder {
    private final SpawnLocationFindResult originalResult;

    @NotNull
    @Contract("_ -> new")
    public static CachedSpawnLocationFinder randomFrom(@NotNull List<SpawnLocationFindResult> locations) {
        return new CachedSpawnLocationFinder(ThreadSafeRandom.randomElement(locations));
    }

    @NotNull
    @Override
    public SpawnLocationFindResult find(@NotNull GameRegion region, int huntersCount) {
        int maxHunters = originalResult.getHuntersLocations().size();
        if (huntersCount > maxHunters) {
            throw new IllegalArgumentException("Max huntersLocations: " + maxHunters + ", requested: " + huntersCount);
        }
        World world = region.getWorld();

        ImmutableLocation runnerLocation = region.withDelta(originalResult.getRunnerLocation().withWorld(world));
        List<ImmutableLocation> huntersLocations = new ArrayList<>(huntersCount);

        List<ImmutableLocation> shuffled = ThreadSafeRandom.shuffled(originalResult.getHuntersLocations());
        for (int i = 0; i < huntersCount; i++) {
            huntersLocations.add(region.withDelta(shuffled.get(i).withWorld(world)));
        }

        ImmutableLocation spectatorsLocation = region.withDelta(originalResult.getSpectatorsLocation().withWorld(world));

        return SpawnLocationFindResult.of(runnerLocation, huntersLocations, spectatorsLocation);

    }
}

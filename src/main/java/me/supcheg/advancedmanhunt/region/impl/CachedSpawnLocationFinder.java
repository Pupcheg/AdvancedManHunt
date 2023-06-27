package me.supcheg.advancedmanhunt.region.impl;

import lombok.Data;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.random.RandomGenerator;

public class CachedSpawnLocationFinder implements SpawnLocationFinder {

    private final CachedSpawnLocation location;
    private final RandomGenerator random;

    public CachedSpawnLocationFinder(@NotNull CachedSpawnLocation location, @NotNull RandomGenerator random) {
        this.location = location;
        this.random = random;
    }

    @NotNull
    @Override
    public Location findForRunner(@NotNull GameRegion region) {
        return region.addDelta(location.getRunnerLocation().clone());
    }

    @NotNull
    @Override
    public Location[] findForHunters(@NotNull GameRegion region, int count) {
        Location[] cachedLocations = location.getHuntersLocations();
        if (count > cachedLocations.length) {
            throw new IllegalArgumentException("%d > %d".formatted(count, cachedLocations.length));
        }
        cachedLocations = cachedLocations.clone();

        Location[] returnLocations = new Location[count];
        for (int i = 0; i < returnLocations.length; i++) {
            Location randomLocation;
            int lastIndex;
            do {
                randomLocation = cachedLocations[lastIndex = random.nextInt(cachedLocations.length)];
            } while (randomLocation == null);

            cachedLocations[lastIndex] = null;
            returnLocations[i] = region.addDelta(randomLocation);
        }

        return returnLocations;
    }

    @NotNull
    @Override
    public Location findForSpectators(@NotNull GameRegion region) {
        return region.addDelta(location.getSpectatorsLocation().clone());
    }

    @Data
    public static class CachedSpawnLocation {
        private final Location runnerLocation;
        private final Location[] huntersLocations;
        private final Location spectatorsLocation;
    }
}

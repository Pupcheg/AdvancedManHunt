package me.supcheg.advancedmanhunt.region.impl;

import lombok.Data;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.random.RandomGenerator;

public class CachedSpawnLocationFinder implements SpawnLocationFinder {

    private final long seed;
    private final RandomGenerator random;
    private final CachedSpawnLocationsEntry entry;

    public CachedSpawnLocationFinder(@NotNull CachedSpawnLocations spawnLocations, @NotNull RandomGenerator random) {
        this.seed = spawnLocations.getSeed();
        this.random = random;

        var entries = spawnLocations.getEntries();
        this.entry = entries.get(random.nextInt(entries.size()));
    }

    private void assertSameSeed(@NotNull GameRegion gameRegion) {
        if (gameRegion.getWorld().getSeed() != seed) {
            throw new IllegalArgumentException();
        }
    }

    @NotNull
    @Override
    public Location findForRunner(@NotNull GameRegion region) {
        assertSameSeed(region);
        return region.addDelta(entry.getRunnerLocation().clone());
    }

    @NotNull
    @Override
    public Location[] findForHunters(@NotNull GameRegion region, int count) {
        assertSameSeed(region);

        Location[] cachedLocations = entry.getHuntersLocations();
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
        assertSameSeed(region);
        return region.addDelta(entry.getSpectatorsLocation().clone());
    }

    @Data
    public static class CachedSpawnLocations {
        private final long seed;
        private final List<CachedSpawnLocationsEntry> entries;
    }

    @Data
    public static class CachedSpawnLocationsEntry {
        private final Location runnerLocation;
        private final Location[] huntersLocations;
        private final Location spectatorsLocation;
    }
}

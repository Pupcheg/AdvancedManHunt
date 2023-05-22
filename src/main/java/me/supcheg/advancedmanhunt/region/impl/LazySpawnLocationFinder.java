package me.supcheg.advancedmanhunt.region.impl;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.random.RandomGenerator;

public class LazySpawnLocationFinder implements SpawnLocationFinder {

    private final RandomGenerator random;

    private static final Vector CENTER = new Vector(0.5, 0, 0.5);
    private final Vector minDistanceFromRunner;
    private final Vector maxDistanceFromRunner;
    private final Distance runnerSpawnRadiusDistance;

    private Location runnerLocation;
    private Location spectatorsLocation;

    public LazySpawnLocationFinder(@NotNull RandomGenerator random,
                                   @NotNull Vector minDistanceFromRunner, @NotNull Vector maxDistanceFromRunner,
                                   @NotNull Distance runnerSpawnRadiusDistance) {
        this.random = random;
        this.minDistanceFromRunner = minDistanceFromRunner;
        this.maxDistanceFromRunner = maxDistanceFromRunner;
        this.runnerSpawnRadiusDistance = runnerSpawnRadiusDistance;
    }

    @NotNull
    @Override
    public Location[] findForHunters(@NotNull GameRegion region, int count) {
        findForRunner(region);
        World world = region.getWorld();

        Location[] locations = new Location[count];

        boolean allValid = false;

        while (!allValid) {
            for (int i = 0; i < count; i++) {
                double offsetY;
                Location hunterLocation = null;

                int leftAttempts = 10;
                boolean currentValid = true;

                do {
                    if (leftAttempts-- <= 0) {
                        currentValid = false;
                        break;
                    }

                    double xOffset = random.nextDouble(minDistanceFromRunner.getX(), maxDistanceFromRunner.getX());
                    double zOffset = random.nextDouble(minDistanceFromRunner.getZ(), maxDistanceFromRunner.getZ());
                    xOffset = random.nextBoolean() ? xOffset : -xOffset;
                    zOffset = random.nextBoolean() ? zOffset : -zOffset;

                    int x = (int) (runnerLocation.getX() + xOffset);
                    int z = (int) (runnerLocation.getZ() + zOffset);
                    hunterLocation = world.getHighestBlockAt(
                            x + region.getStartBlock().getX(),
                            z + region.getStartBlock().getZ(),
                            HeightMap.MOTION_BLOCKING_NO_LEAVES).getLocation();
                    hunterLocation.add(CENTER);

                    offsetY = Math.abs(runnerLocation.getBlockY() - hunterLocation.getBlockY());

                } while (
                        contains(locations, hunterLocation) ||
                                hunterLocation.getBlock().isLiquid() ||
                                offsetY > maxDistanceFromRunner.getY() || offsetY < minDistanceFromRunner.getY());
                if (!currentValid) {
                    allValid = false;
                    refindForRunner(region);
                    break;
                } else {
                    hunterLocation.setY(hunterLocation.getY() + 1);
                    locations[i] = hunterLocation;
                    allValid = true;
                }
            }
        }

        return locations;
    }

    @Contract(pure = true)
    private static <T> boolean contains(@Nullable T @NotNull [] array, @Nullable T value) {
        if (value == null) {
            for (T t : array) {
                if (t == null) {
                    return true;
                }
            }
        } else {
            for (T t : array) {
                if (value.equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Location findForSpectators(@NotNull GameRegion region) {
        if (spectatorsLocation == null) {
            Location location = findForRunner(region);
            location.setX(location.getX() + random.nextDouble(-1, 2));
            location.setY(location.getY() + 15);
            location.setZ(location.getZ() + random.nextDouble(-1, 2));
            spectatorsLocation = location;
        } else if (spectatorsLocation.getWorld() != region.getWorld()) {
            throw new IllegalArgumentException();
        }

        return spectatorsLocation.clone();
    }

    @NotNull
    @Override
    public Location findForRunner(@NotNull GameRegion region) {
        if (runnerLocation == null) {
            refindForRunner(region);
        } else if (runnerLocation.getWorld() != region.getWorld()) {
            throw new IllegalArgumentException();
        }
        return runnerLocation.clone();
    }

    private void refindForRunner(@NotNull GameRegion region) {

        World world = region.getWorld();
        if (runnerLocation != null && runnerLocation.getWorld() != world) {
            throw new IllegalArgumentException();
        }

        int runnerSpawnRadiusBlocks = runnerSpawnRadiusDistance.getBlocks();

        Location temp;
        do {
            int x = random.nextInt(-runnerSpawnRadiusBlocks, runnerSpawnRadiusBlocks + 1);
            int z = random.nextInt(-runnerSpawnRadiusBlocks, runnerSpawnRadiusBlocks + 1);
            temp = world.getHighestBlockAt(
                    x + region.getStartBlock().getX(),
                    z + region.getStartBlock().getZ(),
                    HeightMap.MOTION_BLOCKING_NO_LEAVES).getLocation();
        } while (temp.getBlock().isLiquid());

        temp.add(CENTER);
        temp.setY(temp.getY() + 1);
        runnerLocation = temp;
    }
}

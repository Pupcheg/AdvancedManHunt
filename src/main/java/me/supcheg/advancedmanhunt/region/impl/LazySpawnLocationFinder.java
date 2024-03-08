package me.supcheg.advancedmanhunt.region.impl;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.random.RandomGenerator;

public class LazySpawnLocationFinder implements SpawnLocationFinder {

    private final RandomGenerator random;

    private static final Vector CENTER = new Vector(0.5, 0, 0.5);
    private final Vector minDistanceFromRunner;
    private final Vector maxDistanceFromRunner;
    private final Distance runnerSpawnRadiusDistance;

    private GameRegion region;

    private ImmutableLocation[] huntersLocations;
    private ImmutableLocation runnerLocation;
    private ImmutableLocation spectatorsLocation;

    private SpawnLocationFindResult result;

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
    public SpawnLocationFindResult find(@NotNull GameRegion region, int huntersCount) {
        if (result != null) {
            if (!region.equals(this.region) && huntersCount != huntersLocations.length) {
                throw new IllegalArgumentException();
            }
            return result;
        }

        this.region = region;
        findHuntersAndRunnerLocations(huntersCount);
        findForSpectators();
        this.result = SpawnLocationFindResult.of(runnerLocation, List.of(huntersLocations), spectatorsLocation);
        return result;
    }

    private void findHuntersAndRunnerLocations(int huntersCount) {
        findForRunner();
        World world = region.getWorld();

        huntersLocations = new ImmutableLocation[huntersCount];

        boolean allValid = false;

        while (!allValid) {
            for (int i = 0; i < huntersCount; i++) {
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
                        contains(huntersLocations, hunterLocation) ||
                                hunterLocation.getBlock().isLiquid() ||
                                offsetY > maxDistanceFromRunner.getY() || offsetY < minDistanceFromRunner.getY());
                if (!currentValid) {
                    allValid = false;
                    findForRunner();
                    break;
                } else {
                    hunterLocation.setY(hunterLocation.getY() + 1);
                    huntersLocations[i] = ImmutableLocation.immutableCopy(hunterLocation);
                    allValid = true;
                }
            }
        }
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

    private void findForSpectators() {
        spectatorsLocation = ImmutableLocation.immutableLocation(
                runnerLocation.getWorldReference(),
                runnerLocation.getX() + random.nextDouble(-1, 2),
                runnerLocation.getY() + 15,
                runnerLocation.getZ() + random.nextDouble(-1, 2),
                runnerLocation.getYaw(),
                runnerLocation.getPitch()
        );
    }

    private void findForRunner() {
        World world = region.getWorld();

        int runnerSpawnRadiusBlocks = runnerSpawnRadiusDistance.getBlocks();

        Location mutable;
        do {
            int x = random.nextInt(-runnerSpawnRadiusBlocks, runnerSpawnRadiusBlocks + 1);
            int z = random.nextInt(-runnerSpawnRadiusBlocks, runnerSpawnRadiusBlocks + 1);
            mutable = world.getHighestBlockAt(
                    x + region.getStartBlock().getX(),
                    z + region.getStartBlock().getZ(),
                    HeightMap.MOTION_BLOCKING_NO_LEAVES).getLocation();
        } while (mutable.getBlock().isLiquid());

        mutable.add(CENTER);
        mutable.setY(mutable.getY() + 1);
        runnerLocation = ImmutableLocation.immutableCopy(mutable);
    }
}

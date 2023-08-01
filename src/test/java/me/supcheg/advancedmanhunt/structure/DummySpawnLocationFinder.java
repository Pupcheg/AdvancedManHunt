package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class DummySpawnLocationFinder implements SpawnLocationFinder {

    @NotNull
    @Override
    public SpawnLocationFindResult find(@NotNull GameRegion region, int huntersCount) {
        ImmutableLocation location = zeroWithDelta(region);
        ImmutableLocation[] hunters = new ImmutableLocation[huntersCount];
        Arrays.fill(hunters, location);

        return SpawnLocationFindResult.of(location, List.of(hunters), location);
    }

    private static ImmutableLocation zeroWithDelta(@NotNull GameRegion region) {
        return new ImmutableLocation(
                region.getWorld(),
                region.getCenterBlock().getX(),
                60,
                region.getCenterBlock().getZ()
        );
    }

}

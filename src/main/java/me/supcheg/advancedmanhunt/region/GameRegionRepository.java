package me.supcheg.advancedmanhunt.region;

import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GameRegionRepository {
    Distance MAX_REGION_RADIUS = Distance.ofRegions(32);
    Distance MAX_REGION_SIDE_SIZE = MAX_REGION_RADIUS.add(MAX_REGION_RADIUS).addRegions(1);

    @NotNull
    GameRegion getRegion(@NotNull RealEnvironment environment);

    @Nullable
    GameRegion findRegion(@NotNull Location location);

    @NotNull
    default GameRegion getAndReserveRegion(@NotNull RealEnvironment environment) {
        GameRegion region = getRegion(environment);
        region.setReserved(true);
        return region;
    }
}

package me.supcheg.advancedmanhunt.region;

import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GameRegionRepository extends Listener {
    Distance MAX_REGION_RADIUS = Distance.ofRegions(32);

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

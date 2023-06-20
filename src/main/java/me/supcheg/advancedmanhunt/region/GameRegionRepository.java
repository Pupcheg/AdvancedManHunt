package me.supcheg.advancedmanhunt.region;

import com.google.common.collect.ListMultimap;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public interface GameRegionRepository extends AutoCloseable, Listener {

    Distance MAX_REGION_SIDE_SIZE = Distance.ofRegions(32);

    @NotNull
    GameRegion getRegion(@NotNull Environment environment);

    @Nullable
    GameRegion findRegion(@NotNull Location location);

    @NotNull
    default GameRegion getAndReserveRegion(@NotNull Environment environment) {
        GameRegion region = getRegion(environment);
        region.setReserved(true);
        return region;
    }

    @NotNull
    @UnmodifiableView
    ListMultimap<WorldReference, GameRegion> getRegions();

    @Override
    void close();
}

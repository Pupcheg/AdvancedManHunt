package me.supcheg.advancedmanhunt.region;

import com.google.common.collect.ListMultimap;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

public interface GameRegionRepository extends AutoCloseable, Listener {

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

package me.supcheg.advancedmanhunt.region;

import org.jetbrains.annotations.NotNull;

public interface SpawnLocationFinder {
    @NotNull
    SpawnLocationFindResult find(@NotNull GameRegion region, int huntersCount);
}

package me.supcheg.advancedmanhunt.region;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Can be reused in only SAME game
 */
public interface SpawnLocationFinder {
    @NotNull Location findForRunner(@NotNull GameRegion region);

    @NotNull Location[] findForHunters(@NotNull GameRegion region, int count);

    @NotNull Location findForSpectators(@NotNull GameRegion region);
}

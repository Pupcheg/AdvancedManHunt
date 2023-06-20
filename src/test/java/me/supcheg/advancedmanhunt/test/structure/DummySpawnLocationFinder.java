package me.supcheg.advancedmanhunt.test.structure;

import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.SpawnLocationFinder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class DummySpawnLocationFinder implements SpawnLocationFinder {
    private final Location zeroLocation;

    public DummySpawnLocationFinder() {
        zeroLocation = new Location(null, 0, 60, 0);
    }

    @NotNull
    @Override
    public Location findForRunner(@NotNull GameRegion region) {
        return region.addDelta(zeroLocation.toLocation(region.getWorld()));
    }

    @NotNull
    @Override
    public Location[] findForHunters(@NotNull GameRegion region, int count) {
        Location location = findForRunner(region);

        Location[] locations = new Location[count];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = location.clone();
        }

        return locations;
    }

    @NotNull
    @Override
    public Location findForSpectators(@NotNull GameRegion region) {
        return findForRunner(region);
    }
}

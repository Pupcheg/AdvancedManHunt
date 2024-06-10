package me.supcheg.advancedmanhunt.region;

import lombok.Data;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;

import java.util.List;

@Data(staticConstructor = "of")
public final class SpawnLocationFindResult {
    private final ImmutableLocation runnerLocation;
    private final List<ImmutableLocation> huntersLocations;
    private final ImmutableLocation spectatorsLocation;
}

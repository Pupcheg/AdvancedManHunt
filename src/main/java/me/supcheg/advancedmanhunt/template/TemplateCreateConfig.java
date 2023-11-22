package me.supcheg.advancedmanhunt.template;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.World;

@Builder
@Data
public class TemplateCreateConfig {
    public static final int DEFAULT_SEED = 0;
    public static final int DEFAULT_SPAWN_LOCATIONS_COUNT = 16;
    public static final int DEFAULT_HUNTERS_PER_LOCATIONS = 5;

    private final String name;
    private final Distance sideSize;
    private final World.Environment environment;
    @Builder.Default
    private final long seed = DEFAULT_SEED;
    @Builder.Default
    private final int spawnLocationsCount = DEFAULT_SPAWN_LOCATIONS_COUNT;
    @Builder.Default
    private final int huntersPerLocationCount = DEFAULT_HUNTERS_PER_LOCATIONS;
}

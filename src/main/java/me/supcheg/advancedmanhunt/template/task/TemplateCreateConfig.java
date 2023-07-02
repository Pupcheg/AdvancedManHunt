package me.supcheg.advancedmanhunt.template.task;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class TemplateCreateConfig {
    public static final int DEFAULT_SEED = 0;
    public static final int DEFAULT_SPAWN_LOCATIONS_COUNT = 16;
    public static final int DEFAULT_HUNTERS_PER_LOCATIONS = 5;

    @NotNull
    private final String name;
    @NotNull
    private final Distance sideSize;
    @Builder.Default
    private final long seed = DEFAULT_SEED;
    @NotNull
    private final World.Environment environment;
    @Builder.Default
    private final int spawnLocationsCount = DEFAULT_SPAWN_LOCATIONS_COUNT;
    @Builder.Default
    private final int huntersPerLocationCount = DEFAULT_HUNTERS_PER_LOCATIONS;
}

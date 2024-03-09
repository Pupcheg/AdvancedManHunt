package me.supcheg.advancedmanhunt.template;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.RealEnvironment;

@Builder
@Data
public class TemplateCreateConfig {
    private final String name;
    private final Distance radius;
    private final RealEnvironment environment;
    private final long seed;
    private final int spawnLocationsCount;
    private final int huntersPerLocationCount;
}

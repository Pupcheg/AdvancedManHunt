package me.supcheg.advancedmanhunt.template;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.World;

@Builder
@Data
public class TemplateCreateConfig {
    private final String name;
    private final Distance radius;
    private final World.Environment environment;
    private final long seed;
    private final int spawnLocationsCount;
    private final int huntersPerLocationCount;
}

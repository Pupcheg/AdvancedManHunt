package me.supcheg.advancedmanhunt.template.task;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
public class TemplateCreateConfig {
    @NotNull
    private final String name;
    @NotNull
    private final Distance sideSize;
    @Builder.Default
    private final long seed = 0;
    @NotNull
    private final World.Environment environment;
    @Builder.Default
    private final int spawnLocationsCount = 16;
    @Builder.Default
    private final int huntersPerLocationCount = 5;
}

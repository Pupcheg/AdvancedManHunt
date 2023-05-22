package me.supcheg.advancedmanhunt.template.task;

import lombok.Builder;
import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Builder
@Data
public class TemplateCreateConfig {
    private final String worldName;
    private final Distance sideSize;
    private final long seed;
    @NotNull
    private final World.Environment environment;
    @NotNull
    private final Path out;
}

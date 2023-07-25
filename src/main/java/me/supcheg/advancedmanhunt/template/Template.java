package me.supcheg.advancedmanhunt.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Template {
    @EqualsAndHashCode.Include
    private final String name;
    private final Distance sideSize;
    private final Path folder;
    private final List<CachedSpawnLocationFinder.CachedSpawnLocation> spawnLocations;

    @NotNull
    public Set<Path> getData() throws IOException {
        try (Stream<Path> walk = Files.walk(folder)) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toSet());
        }
    }
}

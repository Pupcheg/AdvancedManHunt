package me.supcheg.advancedmanhunt.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import org.jetbrains.annotations.NotNull;

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
    private final List<SpawnLocationFindResult> spawnLocations;

    @SneakyThrows
    @NotNull
    public Set<Path> getData() {
        try (Stream<Path> walk = Files.walk(folder)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".mca"))
                    .collect(Collectors.toSet());
        }
    }
}

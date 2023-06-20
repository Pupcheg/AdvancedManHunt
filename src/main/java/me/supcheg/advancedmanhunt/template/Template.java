package me.supcheg.advancedmanhunt.template;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(of = "name")
public class Template {
    private final String name;
    private final Distance sideSize;
    private final Path folder;

    @NotNull
    public Set<Path> getData() throws IOException {
        try (Stream<Path> walk = Files.walk(folder)) {
            return walk
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toSet());
        }
    }
}

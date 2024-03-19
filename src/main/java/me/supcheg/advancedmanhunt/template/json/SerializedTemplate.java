package me.supcheg.advancedmanhunt.template.json;

import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@Data
public final class SerializedTemplate {
    private final String name;
    private final Distance radius;
    private final List<SpawnLocationFindResult> spawnLocations;

    @NotNull
    @Contract
    public static SerializedTemplate fromTemplate(@NotNull Template template) {
        return new SerializedTemplate(template.getName(), template.getRadius(), template.getSpawnLocations());
    }

    @NotNull
    @Contract("_ -> new")
    public Template toTemplate(@NotNull Path folder) {
        return new Template(name, radius, folder.toAbsolutePath(), spawnLocations);
    }
}

package me.supcheg.advancedmanhunt.template;

import lombok.Data;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@Data
public final class SerializedTemplate {
    private final Key key;
    private final Distance radius;
    private final List<SpawnLocationFindResult> spawnLocations;

    @NotNull
    @Contract
    public static SerializedTemplate fromTemplate(@NotNull Template template) {
        return new SerializedTemplate(template.getKey(), template.getRadius(), template.getSpawnLocations());
    }

    @NotNull
    @Contract("_ -> new")
    public Template toTemplate(@NotNull Path folder) {
        return new Template(key, radius, folder.toAbsolutePath(), spawnLocations);
    }
}

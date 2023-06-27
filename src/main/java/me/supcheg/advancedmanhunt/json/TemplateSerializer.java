package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocation;
import me.supcheg.advancedmanhunt.template.Template;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

@AllArgsConstructor
public class TemplateSerializer extends TypeAdapter<Template> {

    private static final String KEY = "key";
    private static final String SIDE_SIZE = "side_size";
    private static final String FOLDER = "folder";
    private static final String SPAWN_LOCATIONS = "spawn_locations";

    private static final Type SPAWN_LOCATIONS_LIST_TYPE = Types.type(List.class, CachedSpawnLocation.class);

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Template value) throws IOException {
        out.beginObject();

        out.name(KEY);
        out.value(value.getName());

        out.name(SIDE_SIZE);
        gson.toJson(value.getSideSize(), Distance.class, out);

        out.name(FOLDER);
        out.value(value.getFolder().toString());

        out.name(SPAWN_LOCATIONS);
        gson.toJson(value.getSpawnLocations(), SPAWN_LOCATIONS_LIST_TYPE, out);

        out.endObject();
    }

    @NotNull
    @Override
    public Template read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        String key = null;
        Distance sideSize = null;
        Path folder = null;
        List<CachedSpawnLocationFinder.CachedSpawnLocation> spawnLocations = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case KEY -> key = in.nextString();
                case SIDE_SIZE -> sideSize = gson.fromJson(in, Distance.class);
                case FOLDER -> folder = Path.of(in.nextString());
                case SPAWN_LOCATIONS -> spawnLocations = gson.fromJson(in, SPAWN_LOCATIONS_LIST_TYPE);
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(key, "key");
        Validate.notNull(sideSize, "sideSize");
        Validate.notNull(folder, "folder");
        Validate.notNull(spawnLocations, "spawnLocations");

        return new Template(key, sideSize, folder, spawnLocations);
    }
}

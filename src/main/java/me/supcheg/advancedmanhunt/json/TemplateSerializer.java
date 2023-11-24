package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.template.Template;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TemplateSerializer extends TypeAdapter<Template> {

    private static final String KEY = "key";
    private static final String RADIUS = "radius";
    private static final String FOLDER = "folder";
    private static final String SPAWN_LOCATIONS = "spawn_locations";

    private static final Type SPAWN_LOCATIONS_LIST_TYPE = Types.type(List.class, SpawnLocationFindResult.class);

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Template value) throws IOException {
        out.beginObject();

        out.name(KEY);
        out.value(value.getName());

        out.name(RADIUS);
        gson.toJson(value.getRadius(), Distance.class, out);

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
        Distance radius = null;
        Path folder = null;
        List<SpawnLocationFindResult> spawnLocations = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case KEY -> key = in.nextString();
                case RADIUS -> radius = gson.fromJson(in, Distance.class);
                case FOLDER -> folder = Path.of(in.nextString());
                case SPAWN_LOCATIONS ->
                        spawnLocations = Collections.unmodifiableList(gson.fromJson(in, SPAWN_LOCATIONS_LIST_TYPE));
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(key, "key");
        Validate.notNull(radius, "radius");
        Validate.notNull(folder, "folder");
        Validate.notNull(spawnLocations, "spawnLocations");

        return new Template(key, radius, folder, spawnLocations);
    }
}

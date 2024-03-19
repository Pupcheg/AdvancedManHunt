package me.supcheg.advancedmanhunt.template.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.reflect.Types;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TemplateSerializer extends TypeAdapter<SerializedTemplate> {

    private static final String KEY = "key";
    private static final String RADIUS = "radius";
    private static final String SPAWN_LOCATIONS = "spawn_locations";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull SerializedTemplate value) throws IOException {
        out.beginObject();

        out.name(KEY);
        out.value(value.getName());

        out.name(RADIUS);
        gson.toJson(value.getRadius(), Distance.class, out);

        out.name(SPAWN_LOCATIONS);
        gson.toJson(value.getSpawnLocations(), Types.type(List.class, SpawnLocationFindResult.class), out);

        out.endObject();
    }

    @NotNull
    @Override
    public SerializedTemplate read(@NotNull JsonReader in) throws IOException {
        String key = null;
        Distance radius = null;
        List<SpawnLocationFindResult> spawnLocations = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case KEY -> key = in.nextString();
                case RADIUS -> radius = gson.fromJson(in, Distance.class);
                case SPAWN_LOCATIONS ->
                        spawnLocations = gson.fromJson(in, Types.type(List.class, SpawnLocationFindResult.class));
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(key, "key");
        Validate.notNull(radius, "radius");
        Validate.notNull(spawnLocations, "spawnLocations");

        return new SerializedTemplate(key, radius, Collections.unmodifiableList(spawnLocations));
    }
}

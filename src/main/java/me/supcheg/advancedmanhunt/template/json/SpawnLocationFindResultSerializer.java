package me.supcheg.advancedmanhunt.template.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.reflect.Types;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class SpawnLocationFindResultSerializer extends TypeAdapter<SpawnLocationFindResult> {
    private static final Type IMMUTABLE_LOCATIONS_LIST_TYPE = Types.type(List.class, ImmutableLocation.class);

    private static final String RUNNER = "runner";
    private static final String HUNTERS = "hunters";
    private static final String SPECTATORS = "spectators";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull SpawnLocationFindResult value) throws IOException {
        out.beginObject();

        out.name(RUNNER);
        gson.toJson(value.getRunnerLocation(), ImmutableLocation.class, out);

        out.name(HUNTERS);
        gson.toJson(value.getHuntersLocations(), IMMUTABLE_LOCATIONS_LIST_TYPE, out);

        out.name(SPECTATORS);
        gson.toJson(value.getSpectatorsLocation(), ImmutableLocation.class, out);

        out.endObject();
    }

    @NotNull
    @Override
    public SpawnLocationFindResult read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        ImmutableLocation runner = null;
        List<ImmutableLocation> hunters = null;
        ImmutableLocation spectators = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case RUNNER -> runner = gson.fromJson(in, ImmutableLocation.class);
                case HUNTERS -> hunters = gson.fromJson(in, IMMUTABLE_LOCATIONS_LIST_TYPE);
                case SPECTATORS -> spectators = gson.fromJson(in, ImmutableLocation.class);
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(runner);
        Validate.notNull(hunters);
        Validate.notNull(spectators);

        return SpawnLocationFindResult.of(runner, List.copyOf(hunters), spectators);
    }
}

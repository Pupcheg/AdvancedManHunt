package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocations;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocationsEntry;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@AllArgsConstructor
public class CachedSpawnLocationsSerializer extends TypeAdapter<CachedSpawnLocations> {
    private static final Type ENTRY_LIST_TYPE = Types.type(List.class, CachedSpawnLocationsEntry.class);

    private static final String SEED = "seed";
    private static final String LOCATIONS = "locations";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull CachedSpawnLocations value) throws IOException {
        out.beginObject();

        out.name(SEED);
        out.value(value.getSeed());

        out.name(LOCATIONS);
        gson.toJson(value.getEntries(), ENTRY_LIST_TYPE, out);

        out.endObject();
    }

    @NotNull
    @Override
    public CachedSpawnLocations read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        long seed = -1;
        List<CachedSpawnLocationsEntry> locations = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case SEED -> seed = in.nextLong();
                case LOCATIONS -> locations = gson.fromJson(in, ENTRY_LIST_TYPE);
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.isTrue(seed != -1);
        Validate.notNull(locations);

        return new CachedSpawnLocations(seed, locations);
    }
}

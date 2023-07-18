package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocation;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
public class CachedSpawnLocationSerializer extends TypeAdapter<CachedSpawnLocation> {
    private static final String RUNNER = "runner";
    private static final String HUNTERS = "hunters";
    private static final String SPECTATORS = "spectators";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull CachedSpawnLocation value) throws IOException {
        out.beginObject();

        out.name(RUNNER);
        gson.toJson(value.getRunnerLocation(), Location.class, out);

        out.name(HUNTERS);
        gson.toJson(value.getHuntersLocations(), Location[].class, out);

        out.name(SPECTATORS);
        gson.toJson(value.getSpectatorsLocation(), Location.class, out);

        out.endObject();
    }

    @NotNull
    @Override
    public CachedSpawnLocation read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        Location runner = null;
        Location[] hunters = null;
        Location spectators = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case RUNNER -> runner = gson.fromJson(in, Location.class);
                case HUNTERS -> hunters = gson.fromJson(in, Location[].class);
                case SPECTATORS -> spectators = gson.fromJson(in, Location.class);
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(runner);
        Validate.notNull(hunters);
        Validate.notNull(spectators);

        return new CachedSpawnLocation(runner, hunters, spectators);
    }
}

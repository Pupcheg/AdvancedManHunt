package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
public class GameRegionSerializer extends TypeAdapter<GameRegion> {
    private static final String WORLD = "world";
    private static final String START_REGION = "start_region";
    private static final String END_REGION = "end_region";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull GameRegion value) throws IOException {
        out.beginObject();

        out.name(WORLD);
        out.value(value.getWorldReference().getName());

        out.name(START_REGION);
        gson.toJson(value.getStartRegion(), KeyedCoord.class, out);

        out.name(END_REGION);
        gson.toJson(value.getEndRegion(), KeyedCoord.class, out);

        out.endObject();
    }

    @NotNull
    @Override
    public GameRegion read(@NotNull JsonReader in) throws IOException {
        in.beginObject();

        WorldReference world = null;
        KeyedCoord startRegion = null;
        KeyedCoord endRegion = null;

        while (in.hasNext()) {
            String name = in.nextName();

            switch (name) {
                case WORLD -> world = WorldReference.of(in.nextString());
                case START_REGION -> startRegion = gson.fromJson(in, KeyedCoord.class);
                case END_REGION -> endRegion = gson.fromJson(in, KeyedCoord.class);
                default -> throw new JsonIOException("Invalid token name: " + name);
            }
        }
        in.endObject();

        Validate.notNull(world);
        Validate.notNull(startRegion);
        Validate.notNull(endRegion);

        return new GameRegion(world, startRegion, endRegion);
    }
}

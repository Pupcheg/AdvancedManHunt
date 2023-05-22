package me.supcheg.advancedmanhunt.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class KeyedCoordSerializer extends TypeAdapter<KeyedCoord> {

    @Override
    public void write(@NotNull JsonWriter out, @NotNull KeyedCoord value) throws IOException {
        if (AdvancedManHuntConfig.Serialization.COMPACT_COORDS) {
            out.value(value.getKey());
        } else {
            out.beginArray();
            out.value(value.getX());
            out.value(value.getZ());
            out.endArray();
        }
    }

    @NotNull
    @Override
    public KeyedCoord read(@NotNull JsonReader in) throws IOException {
        if (in.peek() == JsonToken.BEGIN_ARRAY) {
            in.beginArray();
            int x = in.nextInt();
            int z = in.nextInt();
            in.endArray();

            return KeyedCoord.of(x, z);
        } else {
            return KeyedCoord.of(in.nextLong());
        }
    }
}

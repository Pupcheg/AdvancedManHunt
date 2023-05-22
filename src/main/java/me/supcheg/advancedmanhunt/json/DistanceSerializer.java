package me.supcheg.advancedmanhunt.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.coord.Distance;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DistanceSerializer extends TypeAdapter<Distance> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull Distance value) throws IOException {
        out.value(value.getBlocks());
    }

    @NotNull
    @Override
    public Distance read(@NotNull JsonReader in) throws IOException {
        return Distance.ofBlocks(in.nextInt());
    }
}

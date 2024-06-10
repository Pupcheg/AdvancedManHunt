package me.supcheg.advancedmanhunt.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ImmutableLocationAdapter extends TypeAdapter<ImmutableLocation> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull ImmutableLocation value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.value(value.z());
        out.value(value.yaw());
        out.value(value.pitch());
        out.endArray();
    }

    @NotNull
    @Override
    public ImmutableLocation read(@NotNull JsonReader in) throws IOException {
        in.beginArray();
        PositionBuilder builder = PositionBuilder.position()
                .x(in.nextDouble())
                .y(in.nextDouble())
                .z(in.nextDouble())
                .yaw(in.nextDouble())
                .pitch(in.nextDouble());
        in.endArray();
        return builder.immutableLocation();
    }
}

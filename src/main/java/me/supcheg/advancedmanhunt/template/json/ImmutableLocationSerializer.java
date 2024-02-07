package me.supcheg.advancedmanhunt.template.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ImmutableLocationSerializer extends TypeAdapter<ImmutableLocation> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull ImmutableLocation value) throws IOException {
        out.beginArray();
        out.value(value.getX());
        out.value(value.getY());
        out.value(value.getZ());
        out.value(value.getYaw());
        out.value(value.getPitch());
        out.endArray();
    }

    @NotNull
    @Override
    public ImmutableLocation read(@NotNull JsonReader in) throws IOException {
        in.beginArray();
        double x = in.nextDouble();
        double y = in.nextDouble();
        double z = in.nextDouble();

        float yaw = (float) in.nextDouble();
        float pitch = (float) in.nextDouble();
        in.endArray();

        return new ImmutableLocation((WorldReference) null, x, y, z, yaw, pitch);
    }
}

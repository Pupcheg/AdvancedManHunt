package me.supcheg.advancedmanhunt.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LocationSerializer extends TypeAdapter<Location> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull Location value) throws IOException {
        out.beginArray();
        out.value(value.getX());
        out.value(value.getY());
        out.value(value.getZ());
        if (!AdvancedManHuntConfig.Serialization.SHORT_LOCATIONS) {
            out.value(value.getYaw());
            out.value(value.getPitch());
        }
        out.endArray();
    }

    @NotNull
    @Override
    public Location read(@NotNull JsonReader in) throws IOException {
        in.beginArray();
        double x = in.nextDouble();
        double y = in.nextDouble();
        double z = in.nextDouble();

        float yaw;
        float pitch;
        if (in.peek() != JsonToken.END_ARRAY) {
            yaw = (float) in.nextDouble();
            pitch = (float) in.nextDouble();
        } else {
            yaw = pitch = 0;
        }
        in.endArray();

        return new Location(null, x, y, z, yaw, pitch);
    }
}

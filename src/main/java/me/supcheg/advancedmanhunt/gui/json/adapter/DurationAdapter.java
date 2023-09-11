package me.supcheg.advancedmanhunt.gui.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class DurationAdapter extends TypeAdapter<Duration> {
    private static final String INFINITY = "infinity";
    private static final String OVER = "over";

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Duration value) throws IOException {
        if (value.isInfinity()) {
            out.value(INFINITY);
        } else if (value.isOver()) {
            out.value(OVER);
        } else {
            out.value(value.getTicks());
        }
    }

    @NotNull
    @Override
    public Duration read(@NotNull JsonReader in) throws IOException {
        String raw = in.nextString();
        return switch (raw) {
            case INFINITY -> Duration.INFINITY;
            case OVER -> Duration.OVER;
            default -> Duration.ofTicks(Integer.parseInt(raw));
        };
    }
}

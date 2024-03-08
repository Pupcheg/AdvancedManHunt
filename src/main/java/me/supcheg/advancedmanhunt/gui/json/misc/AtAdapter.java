package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AtAdapter extends TypeAdapter<At> {
    private static final String AT = "at";

    @Override
    public void write(@NotNull JsonWriter out, @NotNull At value) throws IOException {
        out.value(value.name().toLowerCase());
    }

    @NotNull
    @Override
    public At read(@NotNull JsonReader in) throws IOException {
        String raw = in.nextString();
        At at = At.valueOf(raw.toUpperCase());
        PropertyHelper.assertNonNull(at, AT, in);
        return at;
    }
}
package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PriorityAdapter extends TypeAdapter<Priority> {
    private static final String PRIORITY = "priority";

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Priority value) throws IOException {
        out.value(value.getValue());
    }

    @NotNull
    @Override
    public Priority read(@NotNull JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NUMBER) {
            return Priority.fromValue(in.nextInt());
        }

        String raw = in.nextString();
        Priority priority = Priority.fromName(raw);
        PropertyHelper.assertNonNull(priority, PRIORITY, in);
        return priority;
    }
}
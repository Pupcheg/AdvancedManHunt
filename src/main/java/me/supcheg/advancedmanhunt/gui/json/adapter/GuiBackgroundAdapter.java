package me.supcheg.advancedmanhunt.gui.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.json.structure.GuiBackground;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

@AllArgsConstructor
public class GuiBackgroundAdapter extends TypeAdapter<GuiBackground> {
    private static final String CHANGE_DURATION = "change_duration";
    private static final String PATH = "path";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull GuiBackground value) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public GuiBackground read(@NotNull JsonReader in) throws IOException {
        Duration changeDuration = null;
        String path = null;

        while (in.hasNext()) {
            switch (in.nextName()) {
                case CHANGE_DURATION -> changeDuration = gson.fromJson(in, Duration.class);
                case PATH -> path = in.nextString();
            }
        }

        Objects.requireNonNull(changeDuration, "changeDuration");
        Objects.requireNonNull(path, "path");

        return new GuiBackground(changeDuration, GuiBackgroundFunction.constant(path));
    }
}

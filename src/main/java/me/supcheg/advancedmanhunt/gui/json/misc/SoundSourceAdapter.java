package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SoundSourceAdapter extends TypeAdapter<Sound.Source> {
    private static final String SOURCE = "source";

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Sound.Source value) throws IOException {
        out.value(value.name().toLowerCase());
    }

    @NotNull
    @Override
    public Sound.Source read(@NotNull JsonReader in) throws IOException {
        String raw = in.nextString();
        Sound.Source source = Sound.Source.valueOf(raw.toUpperCase());
        PropertyHelper.assertNonNull(source, SOURCE, in);
        return source;
    }
}

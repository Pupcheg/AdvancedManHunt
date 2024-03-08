package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.util.Keys;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class KeyAdapter extends TypeAdapter<Key> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull Key value) throws IOException {
        out.value(value.asMinimalString());
    }

    @NotNull
    @Override
    public Key read(@NotNull JsonReader in) throws IOException {
        return Keys.key(in.nextString());
    }
}

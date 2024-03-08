package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.text.Components;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MiniMessageComponentAdapter extends TypeAdapter<Component> {
    @Override
    public void write(@NotNull JsonWriter out, @NotNull Component value) throws IOException {
        out.value(Components.serializeWithNoItalicInfo(value));
    }

    @NotNull
    @Override
    public Component read(@NotNull JsonReader in) throws IOException {
        return Components.deserializeWithNoItalic(in.nextString());
    }
}

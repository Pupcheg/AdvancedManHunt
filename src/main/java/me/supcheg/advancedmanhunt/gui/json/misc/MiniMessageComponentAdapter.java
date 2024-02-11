package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MiniMessageComponentAdapter extends TypeAdapter<Component> {
    public static final MiniMessageComponentAdapter INSTANCE = new MiniMessageComponentAdapter();

    @Override
    public void write(@NotNull JsonWriter out, @NotNull Component value) throws IOException {
        out.value(ComponentUtil.serializeWithNoItalicInfo(value));
    }

    @NotNull
    @Override
    public Component read(@NotNull JsonReader in) throws IOException {
        return ComponentUtil.deserializeWithNoItalic(in.nextString());
    }
}

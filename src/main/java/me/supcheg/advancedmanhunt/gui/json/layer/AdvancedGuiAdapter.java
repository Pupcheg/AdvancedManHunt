package me.supcheg.advancedmanhunt.gui.json.layer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class AdvancedGuiAdapter extends TypeAdapter<AdvancedGui> {
    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull AdvancedGui value) {
        gson.toJson(value.toBuilder(), AdvancedGuiBuilder.class, out);
    }

    @NotNull
    @Override
    public AdvancedGui read(@NotNull JsonReader in) {
        AdvancedGuiBuilder builder = gson.fromJson(in, AdvancedGuiBuilder.class);
        return builder.buildAndRegister();
    }
}
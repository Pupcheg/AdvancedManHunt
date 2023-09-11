package me.supcheg.advancedmanhunt.gui.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickAction;
import me.supcheg.advancedmanhunt.json.Types;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class AdvancedButtonBuilderAdapter extends TypeAdapter<AdvancedButtonBuilder> {
    private static final String SLOTS = "slots";
    private static final String DEFAULT_ENABLED = "default_enabled";
    private static final String DEFAULT_SHOWN = "default_shown";
    private static final String CLICK_ACTIONS = "click_actions";
    private static final String NAME = "name";
    private static final String LORE = "lore";
    private static final String ENCHANTED = "enchanted";
    private static final String RENDERER = "renderer";

    private final AdvancedGuiController controller;
    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull AdvancedButtonBuilder value) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public AdvancedButtonBuilder read(@NotNull JsonReader in) throws IOException {
        AdvancedButtonBuilder builder = controller.button();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case SLOTS -> {
                    if (Objects.requireNonNull(in.peek()) == JsonToken.BEGIN_ARRAY) {
                        gson.fromJson(in, int[].class);
                    } else {
                        builder.slot(in.nextInt());
                    }
                }
                case DEFAULT_ENABLED -> {
                    builder.defaultEnabled(in.nextBoolean());
                }
                case DEFAULT_SHOWN -> {
                    builder.defaultShown(in.nextBoolean());
                }
                case CLICK_ACTIONS -> {
                    var mapType = Types.type(Map.class, String.class, ButtonClickAction.class);
                    Map<String, ButtonClickAction> actions = gson.fromJson(in, mapType);
                    actions.forEach(builder::clickAction);
                }
                case NAME -> {

                }
            }
        }

        return builder;
    }
}

package me.supcheg.advancedmanhunt.gui.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.json.structure.GuiBackground;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@AllArgsConstructor
public class AdvancedGuiBuilderAdapter extends TypeAdapter<AdvancedGuiBuilder> {
    private static final String ROWS = "rows";
    private static final String INDIVIDUAL = "individual";
    private static final String BACKGROUND = "background";
    private static final String BUTTONS = "buttons";

    private final AdvancedGuiController controller;
    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull AdvancedGuiBuilder value) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder read(@NotNull JsonReader in) throws IOException {
        AdvancedGuiBuilder builder = controller.gui();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case ROWS -> {
                    builder.rows(in.nextInt());
                }
                case INDIVIDUAL -> {
                    if (in.nextBoolean()) {
                        builder.individual();
                    }
                }
                case BACKGROUND -> {
                    GuiBackground guiBackground = gson.fromJson(in, GuiBackground.class);
                    builder.lazyAnimatedBackground(guiBackground.getFunction(), guiBackground.getChangePeriod());
                }
                case BUTTONS -> {
                    AdvancedButtonBuilder[] buttons = gson.fromJson(in, AdvancedButtonBuilder[].class);
                    for (AdvancedButtonBuilder button : buttons) {
                        builder.button(button);
                    }
                }
            }
        }

        return builder;
    }
}

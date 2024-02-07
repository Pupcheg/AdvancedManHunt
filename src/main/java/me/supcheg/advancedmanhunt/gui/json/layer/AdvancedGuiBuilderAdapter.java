package me.supcheg.advancedmanhunt.gui.json.layer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;
import me.supcheg.advancedmanhunt.gui.api.tick.GuiTicker;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class AdvancedGuiBuilderAdapter extends TypeAdapter<AdvancedGuiBuilder> {
    private static final String KEY = "key";
    private static final String ROWS = "rows";
    private static final String BACKGROUND = "background";
    private static final String BUTTONS = "buttons";
    private static final String TICKERS = "tickers";

    private final Gson gson;
    private final AdvancedGuiController controller;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull AdvancedGuiBuilder value) throws IOException {
        out.beginObject();

        out.name(KEY);
        out.value(value.getKey());

        out.name(ROWS);
        out.value(value.getRows());

        out.name(BACKGROUND);
        gson.toJson(value.getBackgroundFunction(), GuiBackgroundFunction.class, out);

        out.name(BUTTONS);
        gson.toJson(value.getButtons(), Types.type(List.class, AdvancedButtonBuilder.class), out);

        out.name(TICKERS);
        gson.toJson(value.getTickers(), Types.type(List.class, GuiTicker.class), out);

        out.endObject();
    }

    @NotNull
    @Override
    public AdvancedGuiBuilder read(@NotNull JsonReader in) throws IOException {
        String key = null;
        Integer rows = null;
        GuiBackgroundFunction background = null;
        List<AdvancedButtonBuilder> buttons = Collections.emptyList();
        List<GuiTicker> tickers = Collections.emptyList();

        in.beginObject();
        while (in.hasNext()) {
            String name = JsonUtil.nextNonDollarName(in);
            switch (name) {
                case KEY -> key = in.nextString();
                case ROWS -> rows = in.nextInt();
                case BACKGROUND -> background = gson.fromJson(in, GuiBackgroundFunction.class);
                case BUTTONS -> buttons = gson.fromJson(in, Types.type(List.class, AdvancedButtonBuilder.class));
                case TICKERS -> tickers = gson.fromJson(in, Types.type(List.class, GuiTicker.class));
                default -> throw PropertyHelper.unknownNameException(name, in);
            }
        }
        in.endObject();

        PropertyHelper.assertNonNull(key, KEY, in);
        PropertyHelper.assertNonNull(rows, ROWS, in);
        PropertyHelper.assertNonNull(buttons, BUTTONS, in);
        PropertyHelper.assertNonNull(tickers, TICKERS, in);

        AdvancedGuiBuilder builder = controller.gui();
        builder.key(key);
        builder.rows(rows);
        PropertyHelper.apply(builder::background, background);
        buttons.forEach(builder::button);
        tickers.forEach(builder::ticker);

        return builder;
    }
}
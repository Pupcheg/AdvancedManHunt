package me.supcheg.advancedmanhunt.gui.json.functional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class DefaultButtonConfigurerAdapter extends TypeAdapter<DefaultButtonConfigurer> {
    private static final String CLICK_ACTIONS = "click_actions";
    private static final String TICKERS = "tickers";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull DefaultButtonConfigurer value) throws IOException {
        out.beginObject();

        out.name(CLICK_ACTIONS);
        gson.toJson(value.getClickActions(), Types.type(List.class, ButtonClickAction.class), out);

        out.name(TICKERS);
        gson.toJson(value.getTickers(), Types.type(List.class, ButtonTicker.class), out);

        out.endObject();
    }

    @Override
    public DefaultButtonConfigurer read(@NotNull JsonReader in) throws IOException {
        List<ButtonClickAction> clickActions = Collections.emptyList();
        List<ButtonTicker> tickers = Collections.emptyList();

        in.beginObject();

        while (in.hasNext()) {
            String name = JsonUtil.nextNonDollarName(in);
            switch (name) {
                case CLICK_ACTIONS -> clickActions = gson.fromJson(in, Types.type(List.class, ButtonClickAction.class));
                case TICKERS -> tickers = gson.fromJson(in, Types.type(List.class, ButtonTicker.class));
                default -> throw PropertyHelper.unknownNameException(name, in);
            }
        }

        in.endObject();

        PropertyHelper.assertNonNull(clickActions, CLICK_ACTIONS, in);
        PropertyHelper.assertNonNull(tickers, TICKERS, in);

        return new DefaultButtonConfigurer(clickActions, tickers);
    }
}

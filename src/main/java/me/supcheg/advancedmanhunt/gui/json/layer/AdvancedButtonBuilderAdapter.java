package me.supcheg.advancedmanhunt.gui.json.layer;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonLoreFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonNameFunction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonTextureFunction;
import me.supcheg.advancedmanhunt.gui.api.tick.ButtonTicker;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.reflect.Types;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class AdvancedButtonBuilderAdapter extends TypeAdapter<AdvancedButtonBuilder> {
    private static final String SLOTS = "slots";
    private static final String SLOT = "slot";
    private static final String DEFAULT_ENABLED = "default_enabled";
    private static final String DEFAULT_SHOWN = "default_shown";
    private static final String CLICK_ACTIONS = "click_actions";
    private static final String TEXTURE = "texture";
    private static final String NAME = "name";
    private static final String LORE = "lore";
    private static final String TICKERS = "tickers";
    private static final String DEFAULT_ENCHANTED = "default_enchanted";

    private final Gson gson;
    private final AdvancedGuiController controller;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull AdvancedButtonBuilder value) throws IOException {
        out.beginObject();

        out.name(SLOTS);
        gson.toJson(value.getSlots().intStream(), IntStream.class, out);

        out.name(DEFAULT_ENABLED);
        out.value(value.getDefaultEnabled());

        out.name(DEFAULT_SHOWN);
        out.value(value.getDefaultShown());

        out.name(CLICK_ACTIONS);
        gson.toJson(value.getClickActions(), Types.type(List.class, ButtonClickAction.class), out);

        out.name(TEXTURE);
        gson.toJson(value.getTextureFunction(), ButtonTextureFunction.class, out);

        out.name(NAME);
        gson.toJson(value.getNameFunction(), ButtonNameFunction.class, out);

        out.name(LORE);
        gson.toJson(value.getLoreFunction(), ButtonLoreFunction.class, out);

        out.name(TICKERS);
        gson.toJson(value.getTickers(), Types.type(List.class, ButtonTicker.class), out);

        out.name(DEFAULT_ENCHANTED);
        out.value(value.getDefaultEnchanted());

        out.endObject();
    }

    @NotNull
    @Override
    public AdvancedButtonBuilder read(@NotNull JsonReader in) throws IOException {
        IntStream slots = null;
        Boolean defaultEnabled = null;
        Boolean defaultShown = null;
        List<ButtonClickAction> clickActions = Collections.emptyList();
        ButtonTextureFunction texture = null;
        ButtonNameFunction nameFunction = null;
        ButtonLoreFunction lore = null;
        List<ButtonTicker> tickers = Collections.emptyList();
        Boolean defaultEnchanted = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = JsonUtil.nextNonDollarName(in);
            switch (name) {
                case SLOTS, SLOT -> slots = gson.fromJson(in, IntStream.class);
                case DEFAULT_ENABLED -> defaultEnabled = in.nextBoolean();
                case DEFAULT_SHOWN -> defaultShown = in.nextBoolean();
                case CLICK_ACTIONS -> clickActions = gson.fromJson(in, Types.type(List.class, ButtonClickAction.class));
                case TEXTURE -> texture = gson.fromJson(in, ButtonTextureFunction.class);
                case NAME -> nameFunction = gson.fromJson(in, ButtonNameFunction.class);
                case LORE -> lore = gson.fromJson(in, ButtonLoreFunction.class);
                case TICKERS -> tickers = gson.fromJson(in, Types.type(List.class, ButtonTicker.class));
                case DEFAULT_ENCHANTED -> defaultEnchanted = in.nextBoolean();
                default -> throw PropertyHelper.unknownNameException(name, in);
            }
        }
        in.endObject();

        PropertyHelper.assertNonNull(slots, SLOTS, in);
        PropertyHelper.assertNonNull(clickActions, CLICK_ACTIONS, in);
        PropertyHelper.assertNonNull(texture, TEXTURE, in);
        PropertyHelper.assertNonNull(tickers, TICKERS, in);

        AdvancedButtonBuilder builder = controller.button();
        builder.slot(slots);
        PropertyHelper.apply(builder::defaultEnabled, defaultEnabled);
        PropertyHelper.apply(builder::defaultShown, defaultShown);
        clickActions.forEach(builder::clickAction);
        PropertyHelper.apply(builder::texture, texture);
        PropertyHelper.apply(builder::name, nameFunction);
        PropertyHelper.apply(builder::lore, lore);
        tickers.forEach(builder::ticker);
        PropertyHelper.apply(builder::defaultEnchanted, defaultEnchanted);

        return builder;
    }
}
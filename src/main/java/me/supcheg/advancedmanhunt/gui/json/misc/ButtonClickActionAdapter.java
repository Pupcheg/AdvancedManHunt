package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.ButtonClickAction;
import me.supcheg.advancedmanhunt.gui.api.functional.ButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class ButtonClickActionAdapter extends TypeAdapter<ButtonClickAction> {
    private static final String PRIORITY = "priority";
    private static final String CONSUMER = "consumer";

    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull ButtonClickAction value) throws IOException {
        out.beginObject();

        out.name(PRIORITY);
        gson.toJson(value.getPriority(), Priority.class, out);

        out.name(CONSUMER);
        gson.toJson(value.getConsumer(), ButtonClickActionConsumer.class, out);

        out.endObject();
    }

    @NotNull
    @Override
    public ButtonClickAction read(@NotNull JsonReader in) throws IOException {
        Priority priority = Priority.NORMAL;
        ButtonClickActionConsumer consumer = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = JsonUtil.nextNonDollarName(in);
            switch (name) {
                case PRIORITY -> priority = gson.fromJson(in, Priority.class);
                case CONSUMER -> consumer = gson.fromJson(in, ButtonClickActionConsumer.class);
                default -> throw PropertyHelper.unknownNameException(name, in);
            }
        }
        in.endObject();

        PropertyHelper.assertNonNull(priority, PRIORITY, in);
        PropertyHelper.assertNonNull(consumer, CONSUMER, in);

        return new ButtonClickAction(priority, consumer);
    }

}
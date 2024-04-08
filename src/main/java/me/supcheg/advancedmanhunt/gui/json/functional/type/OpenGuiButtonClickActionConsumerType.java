package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.functional.action.OpenGuiButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.util.JsonReaders;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@RequiredArgsConstructor
public class OpenGuiButtonClickActionConsumerType implements FunctionalAdapterType<OpenGuiButtonClickActionConsumer> {
    public static final String NAME = "open";
    private static final String KEY = "key";

    private final Gson gson;

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof OpenGuiButtonClickActionConsumer;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull OpenGuiButtonClickActionConsumer value) throws IOException {
        out.name(KEY);
        gson.toJson(value.getKey(), Key.class, out);
    }

    @NotNull
    @Override
    public OpenGuiButtonClickActionConsumer read(@NotNull JsonReader in) throws IOException {
        Key key = null;

        while (in.hasNext()) {
            String cursor = JsonReaders.nextNonDollarName(in);
            if (KEY.equals(cursor)) {
                key = gson.fromJson(in, Key.class);
            } else {
                throw PropertyHelper.unknownNameException(cursor, in);
            }
        }

        PropertyHelper.assertNonNull(key, KEY, in);

        return new OpenGuiButtonClickActionConsumer(key);
    }
}
package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.action.ClickActions;
import me.supcheg.advancedmanhunt.gui.api.functional.action.OpenGuiButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class OpenGuiButtonClickActionConsumerType implements FunctionalAdapterType<OpenGuiButtonClickActionConsumer> {
    public static final String NAME = "open";
    private static final String KEY = "key";

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof OpenGuiButtonClickActionConsumer;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull OpenGuiButtonClickActionConsumer value) throws IOException {
        out.name(KEY);
        out.value(value.getKey());
    }

    @NotNull
    @Override
    public OpenGuiButtonClickActionConsumer read(@NotNull JsonReader in) throws IOException {
        return ClickActions.openGui(PropertyHelper.readString(in, KEY));
    }
}
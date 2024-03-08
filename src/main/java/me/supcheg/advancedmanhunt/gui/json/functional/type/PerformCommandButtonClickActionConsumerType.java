package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.api.functional.action.PerformCommandButtonClickActionConsumer;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PerformCommandButtonClickActionConsumerType implements FunctionalAdapterType<PerformCommandButtonClickActionConsumer> {
    public static final String NAME = "perform_command";
    private static final String LABEL = "label";

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof PerformCommandButtonClickActionConsumer;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull PerformCommandButtonClickActionConsumer value) throws IOException {
        out.name(LABEL);
        out.value(value.getLabel());
    }

    @NotNull
    @Override
    public PerformCommandButtonClickActionConsumer read(@NotNull JsonReader in) throws IOException {
        return new PerformCommandButtonClickActionConsumer(PropertyHelper.readString(in, LABEL));
    }
}
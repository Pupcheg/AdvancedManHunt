package me.supcheg.advancedmanhunt.gui.json.functional;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.supcheg.advancedmanhunt.gui.json.BadPropertyException;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import me.supcheg.advancedmanhunt.util.Unchecked;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class FunctionalAdapter<I> extends TypeAdapter<I> {
    private static final String TYPE = "type";

    private final Map<String, FunctionalAdapterType<? extends I>> adapters;

    @SafeVarargs
    public FunctionalAdapter(@NotNull FunctionalAdapterType<? extends I>... adapters) {
        this.adapters = Arrays.stream(adapters).collect(Collectors.toMap(FunctionalAdapterType::getName, UnaryOperator.identity()));
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull I value) throws IOException {
        for (Map.Entry<String, FunctionalAdapterType<? extends I>> entry : adapters.entrySet()) {
            if (entry.getValue().canWrite(value)) {
                out.beginObject();

                out.name(TYPE);
                out.value(entry.getKey());

                entry.getValue().write(out, Unchecked.uncheckedCast(value));

                out.endObject();
                return;
            }
        }

        throw new IllegalArgumentException("Unable write " + value);
    }

    @NotNull
    @Override
    public I read(@NotNull JsonReader in) throws IOException {
        String type = null;

        in.beginObject();
        if (JsonUtil.nextNonDollarName(in).equals(TYPE)) {
            type = in.nextString();
        }
        PropertyHelper.assertNonNull(type, TYPE, in);

        FunctionalAdapterType<? extends I> adapter = adapters.get(type);
        if (adapter == null) {
            throw new BadPropertyException(
                    "Type '%s' is not supported".formatted(type)
            );
        }
        I functionalInterface = adapter.read(in);

        in.endObject();

        return functionalInterface;
    }
}
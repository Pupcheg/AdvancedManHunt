package me.supcheg.advancedmanhunt.gui.json.misc;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.api.sequence.At;
import me.supcheg.advancedmanhunt.gui.api.sequence.Priority;
import me.supcheg.advancedmanhunt.gui.api.tick.AbstractTicker;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class TickerAdapter<T extends AbstractTicker<T, C>, C extends Consumer<?>> extends TypeAdapter<T> {
    private static final String AT = "at";
    private static final String PRIORITY = "priority";
    private static final String CONSUMER = "consumer";

    private final Class<C> consumerClass;
    private final TickerBuilder<T, C> tickerBuilder;
    private final Gson gson;

    @Override
    public void write(@NotNull JsonWriter out, @NotNull T value) throws IOException {
        out.beginObject();

        out.name(AT);
        gson.toJson(value.getAt(), At.class, out);

        out.name(PRIORITY);
        gson.toJson(value.getPriority(), Priority.class, out);

        out.name(CONSUMER);
        gson.toJson(value.getConsumer(), consumerClass, out);

        out.endObject();
    }

    @NotNull
    @Override
    public T read(@NotNull JsonReader in) throws IOException {
        At at = null;
        Priority priority = Priority.NORMAL;
        C consumer = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = JsonUtil.nextNonDollarName(in);
            switch (name) {
                case AT -> at = gson.fromJson(in, At.class);
                case PRIORITY -> priority = gson.fromJson(in, Priority.class);
                case CONSUMER -> consumer = gson.fromJson(in, consumerClass);
                default -> throw PropertyHelper.unknownNameException(name, in);
            }
        }
        in.endObject();

        PropertyHelper.assertNonNull(at, AT, in);
        PropertyHelper.assertNonNull(priority, PRIORITY, in);
        PropertyHelper.assertNonNull(consumer, CONSUMER, in);

        return tickerBuilder.build(at, priority, consumer);
    }

    public interface TickerBuilder<T extends AbstractTicker<T, C>, C extends Consumer<?>> {
        @NotNull
        T build(@NotNull At at, @NotNull Priority priority, @NotNull C consumer);
    }
}
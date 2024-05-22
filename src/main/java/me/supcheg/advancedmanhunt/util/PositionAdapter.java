package me.supcheg.advancedmanhunt.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.papermc.paper.math.Position;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.math.Positions;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;

@RequiredArgsConstructor
public class PositionAdapter<T extends Position> {
    private static final String SPAWN = "spawn";
    @Language("RegExp")
    private static final String RAW_PATTERN = ("([\\w_/\\\\-]+)" +
                                               "\\[" +
                                               "(%s|[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+))" +
                                               "(, *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+))?" +
                                               "]").formatted(SPAWN);
    private static final Pattern LOCATION_PATTERN = Pattern.compile(RAW_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern COMMA_PATTERN = Pattern.compile(", *");

    private final Class<T> type;
    private final Function<PositionBuilder, T> builder;

    @NotNull
    public Class<T> type() {
        return type;
    }

    @NotNull
    public String serialize(@Nullable T position) throws IOException {
        StringWriter writer = new StringWriter(32);
        serialize(position, writer);
        return writer.toString();
    }

    public void serialize(@Nullable T position, @NotNull Writer dest) throws IOException {
        if (position == null) {
            dest.append(null);
            return;
        }

        PositionBuilder builder = position(position);

        WorldReference world = builder.world();
        dest.append(world == null ? null : world.getName());

        dest.append('[');
        if (world != null && Positions.equals(builder, world.getWorld().getSpawnLocation())) {
            dest.append(SPAWN);
        } else {
            dest.append(String.valueOf(builder.x()))
                    .append(", ")
                    .append(String.valueOf(builder.y()))
                    .append(", ")
                    .append(String.valueOf(builder.z()));
        }

        if (Double.compare(builder.yaw(), 0) != 0 || Double.compare(builder.pitch(), 0) != 0) {
            dest.append(", ")
                    .append(String.valueOf(builder.yaw()))
                    .append(", ")
                    .append(String.valueOf(builder.pitch()));
        }
        dest.append(']');
    }

    @Nullable
    @Contract("!null -> !null; null -> null")
    public T deserialize(@Nullable String raw) throws IOException {
        if (raw == null) {
            return null;
        }

        Matcher matcher = LOCATION_PATTERN.matcher(raw);

        if (!matcher.matches()) {
            throw new IOException(raw + " is not a valid location");
        }

        String worldName = matcher.group(1);
        String coords = matcher.group(2);
        String direction = matcher.group(3);

        WorldReference world = WorldReference.of(worldName);
        PositionBuilder builder = PositionBuilder.position().world(world);

        if (coords.equalsIgnoreCase(SPAWN)) {
            Location spawnLocation = world.getWorld().getSpawnLocation();
            builder.xyz(spawnLocation);
        } else {
            String[] coordsSplit = COMMA_PATTERN.split(coords, 3);
            builder.x(Double.parseDouble(coordsSplit[0]))
                    .y(Double.parseDouble(coordsSplit[1]))
                    .z(Double.parseDouble(coordsSplit[2]));
        }

        if (direction != null) {
            String[] rawDirection = COMMA_PATTERN.split(direction, 3);

            builder.yaw(Float.parseFloat(rawDirection[1]))
                    .pitch(Float.parseFloat(rawDirection[2]));
        }
        return this.builder.apply(builder);
    }

    @NotNull
    @Contract("-> new")
    public ScalarSerializer<T> asConfigurateSerializer() {
        return new ScalarSerializer<>(type) {
            @Override
            public T deserialize(Type type, Object obj) throws SerializationException {
                try {
                    return PositionAdapter.this.deserialize(String.valueOf(obj));
                } catch (IOException e) {
                    throw new SerializationException(e);
                }
            }

            @SneakyThrows
            @Override
            protected Object serialize(T item, Predicate<Class<?>> typeSupported) {
                return PositionAdapter.this.serialize(item);
            }
        };
    }

    @NotNull
    @Contract("-> new")
    public TypeAdapter<T> asGsonAdapter() {
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.value(PositionAdapter.this.serialize(value));
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return PositionAdapter.this.deserialize(in.nextString());
            }
        };
    }
}

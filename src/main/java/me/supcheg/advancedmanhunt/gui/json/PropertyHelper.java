package me.supcheg.advancedmanhunt.gui.json;

import com.google.gson.stream.JsonReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.util.JsonUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyHelper {
    @Contract(value = "null, _, _ -> fail; !null, _, _ -> _", pure = true)
    public static void assertNonNull(@Nullable Object o, @NotNull String name, @NotNull JsonReader reader)
            throws BadPropertyException {
        Objects.requireNonNull(name, "name");
        if (o == null) {
            throw new BadPropertyException(
                    "Property '" + name + "' is not set or configured wrongly" +
                            JsonUtil.getLocationString(reader)
            );
        }
    }

    @NotNull
    public static BadPropertyException unknownNameException(@NotNull String name, @NotNull JsonReader reader) {
        return new BadPropertyException("Unknown property name: '" + name + "'" + JsonUtil.getLocationString(reader));
    }

    @NotNull
    public static String readString(@NotNull JsonReader in, @NotNull String name) throws IOException {
        String value = null;
        if (JsonUtil.nextNonDollarName(in).equals(name)) {
            value = in.nextString();
        }
        assertNonNull(value, name, in);
        return value;
    }

    public static <A> void apply(@NotNull Consumer<A> consumer, @Nullable A arg) {
        Objects.requireNonNull(consumer, "consumer");
        if (arg != null) {
            consumer.accept(arg);
        }
    }
}

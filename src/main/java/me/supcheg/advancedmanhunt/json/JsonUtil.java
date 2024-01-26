package me.supcheg.advancedmanhunt.json;

import com.google.gson.stream.JsonReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {
    private static final MethodHandle jsonReader_locationString;
    private static final MethodHandle jsonReader_in;

    static {
        try {
            Method locationString = JsonReader.class.getDeclaredMethod("locationString");
            locationString.trySetAccessible();
            jsonReader_locationString = MethodHandles.lookup().unreflect(locationString);

            Field in = JsonReader.class.getDeclaredField("in");
            in.trySetAccessible();
            jsonReader_in = MethodHandles.lookup().unreflectGetter(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @NotNull
    public static String getLocationString(@NotNull JsonReader reader) {
        return (String) jsonReader_locationString.invoke(reader);
    }

    @SneakyThrows
    @NotNull
    public static Reader getIn(@NotNull JsonReader reader) {
        return (Reader) jsonReader_in.invoke(reader);
    }
}

package me.supcheg.advancedmanhunt.json;

import com.google.gson.stream.JsonReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {
    private static final MethodHandle jsonReader_locationString;

    static {
        try {
            Method locationString = JsonReader.class.getDeclaredMethod("locationString");
            locationString.trySetAccessible();
            jsonReader_locationString = MethodHandles.lookup().unreflect(locationString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @NotNull
    public static String getLocationString(@NotNull JsonReader reader) {
        return (String) jsonReader_locationString.invoke(reader);
    }
}

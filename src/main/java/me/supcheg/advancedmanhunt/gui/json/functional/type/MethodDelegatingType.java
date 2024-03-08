package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.json.BadPropertyException;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodDelegatingFunctionalInterface;
import me.supcheg.advancedmanhunt.util.JsonReaders;
import me.supcheg.advancedmanhunt.reflect.MethodHandleLookup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class MethodDelegatingType<I extends MethodDelegatingFunctionalInterface> implements FunctionalAdapterType<I> {
    public static final String NAME = "reflect";
    private static final String METHOD = "method";

    private static final Pattern PATTERN = Pattern.compile(
            "((?:[\\p{L}_$][\\p{L}\\p{N}_$]*\\.)*[\\p{L}_$][\\p{L}\\p{N}_$]*)" + // class
                    "#([\\p{L}_$][\\p{L}\\p{N}_$]*)" + // method
                    "\\(([\\p{L}_$][\\p{L}\\p{N}_$]*)\\)" // parameter
    );

    private final BiFunction<String, MethodHandle, I> interfaceConstructor;
    private final Class<?> expectedParameter;
    private final MethodHandleLookup lookup;

    @NotNull
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canWrite(@NotNull Object obj) {
        return obj instanceof MethodDelegatingFunctionalInterface;
    }

    @Override
    public void write(@NotNull JsonWriter out, @NotNull I value) throws IOException {
        out.name(METHOD);
        out.value(value.getSerialized());
    }

    @NotNull
    @Override
    public I read(@NotNull JsonReader in) throws IOException {
        String full = PropertyHelper.readString(in, METHOD);
        Matcher matcher = PATTERN.matcher(full);
        if (!matcher.find()) {
            throw PropertyHelper.wrongConfiguredException(METHOD, in);
        }

        Class<?> clazz = findClass(matcher.group(1), in);
        String methodName = matcher.group(2);
        String parameterName = matcher.group(3);

        if (!expectedParameter.getSimpleName().equals(parameterName)) {
            throw new BadPropertyException("Expected %s as parameter name, got: '%s'%s"
                    .formatted(expectedParameter.getSimpleName(), parameterName, JsonReaders.getLocationString(in))
            );
        }

        return interfaceConstructor.apply(full, lookup.findMethod(clazz, methodName, expectedParameter));
    }

    @NotNull
    private Class<?> findClass(@NotNull String className, @NotNull JsonReader in) throws IOException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BadPropertyException("Class not found: " + className + JsonReaders.getLocationString(in), e);
        }
    }
}
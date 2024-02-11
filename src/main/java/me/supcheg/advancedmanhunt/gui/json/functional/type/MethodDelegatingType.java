package me.supcheg.advancedmanhunt.gui.json.functional.type;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.gui.json.PropertyHelper;
import me.supcheg.advancedmanhunt.gui.json.functional.FunctionalAdapterType;
import me.supcheg.advancedmanhunt.gui.json.functional.method.MethodDelegatingFunctionalInterface;
import me.supcheg.advancedmanhunt.util.reflect.MethodHandleLookup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class MethodDelegatingType<I extends MethodDelegatingFunctionalInterface> implements FunctionalAdapterType<I> {
    public static final String NAME = "reflect";
    private static final String METHOD = "method";

    private final BiFunction<String, Supplier<MethodHandle>, I> interfaceConstructor;
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
        out.value(value.getMethodName());
    }

    @NotNull
    @Override
    public I read(@NotNull JsonReader in) throws IOException {
        String name = PropertyHelper.readString(in, METHOD);
        return interfaceConstructor.apply(name, lookup.findMethod(name));
    }
}
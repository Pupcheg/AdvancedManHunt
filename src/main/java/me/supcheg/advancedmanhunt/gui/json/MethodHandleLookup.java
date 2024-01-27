package me.supcheg.advancedmanhunt.gui.json;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedFunction;

@RequiredArgsConstructor
public class MethodHandleLookup {
    private final Object obj;

    @NotNull
    public MethodHandle findMethodHandle(@NotNull String name) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .peek(Method::trySetAccessible)
                .findFirst()
                .map(uncheckedFunction(MethodHandles.lookup()::unreflect))
                .map(handle -> handle.bindTo(obj))
                .orElseThrow();
    }
}

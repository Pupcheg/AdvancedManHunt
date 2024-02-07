package me.supcheg.advancedmanhunt.util.reflect;

import com.google.common.base.Suppliers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class InstantMethodHandleLookup implements MethodHandleLookup {
    private final Object obj;

    @NotNull
    @Override
    public Supplier<MethodHandle> findMethod(@NotNull String name) {
        return Suppliers.ofInstance(
                Arrays.stream(obj.getClass().getDeclaredMethods())
                        .filter(m -> m.getName().equals(name))
                        .peek(Method::trySetAccessible)
                        .findFirst()
                        .map(this::unreflectMethod)
                        .map(handle -> handle.bindTo(obj))
                        .orElseThrow()
        );
    }

    @SneakyThrows
    @NotNull
    private MethodHandle unreflectMethod(@NotNull Method method) {
        return MethodHandles.lookup().unreflect(method);
    }
}

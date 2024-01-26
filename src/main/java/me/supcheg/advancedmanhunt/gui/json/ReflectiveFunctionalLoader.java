package me.supcheg.advancedmanhunt.gui.json;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public class ReflectiveFunctionalLoader {
    private final Object obj;

    @SneakyThrows
    public <I> I create(@NotNull String methodName, @NotNull Function<MethodHandle, I> constructor) {
        Method method = findMethod(methodName);
        method.trySetAccessible();
        MethodHandle handle = MethodHandles.lookup().unreflect(method).bindTo(obj);
        return constructor.apply(handle);
    }

    @NotNull
    private Method findMethod(@NotNull String name) {
        return Arrays.stream(obj.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .peek(Method::trySetAccessible)
                .findFirst()
                .orElseThrow();
    }
}

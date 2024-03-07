package me.supcheg.advancedmanhunt.util.reflect;

import lombok.AccessLevel;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@CustomLog
public class InstantMethodHandleLookup implements MethodHandleLookup {
    public static final InstantMethodHandleLookup INSTANCE = new InstantMethodHandleLookup();

    @NotNull
    @Override
    public MethodHandle findMethod(@NotNull Class<?> clazz, @NotNull String name, @NotNull Type parameterType) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> m.getParameterTypes()[0].equals(parameterType))
                .peek(Method::trySetAccessible)
                .peek(this::warnIfNoAnnotation)
                .findFirst()
                .map(this::unreflectMethod)
                .orElseThrow();
    }

    @SneakyThrows
    @NotNull
    private MethodHandle unreflectMethod(@NotNull Method method) {
        return MethodHandles.lookup().unreflect(method);
    }

    private void warnIfNoAnnotation(@NotNull Method method) {
        if (!method.isAnnotationPresent(ReflectCalled.class)) {
            log.warn(
                    "Method {} is used by {}, but isn't marked with {} annotation",
                    method, MethodHandleLookup.class.getSimpleName(), ReflectCalled.class.getSimpleName()
            );
        }
    }
}

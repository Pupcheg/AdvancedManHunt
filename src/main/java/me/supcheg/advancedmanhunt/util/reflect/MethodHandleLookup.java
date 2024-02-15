package me.supcheg.advancedmanhunt.util.reflect;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

public interface MethodHandleLookup {
    @NotNull
    MethodHandle findMethod(@NotNull Class<?> clazz, @NotNull String name, @NotNull Type parameterType);
}

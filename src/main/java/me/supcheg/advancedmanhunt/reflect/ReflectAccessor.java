package me.supcheg.advancedmanhunt.reflect;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ReflectAccessor {

    @NotNull
    String resolveClassName(@NotNull String partial);

    @SneakyThrows
    @NotNull
    default Class<?> resolveClass(@NotNull String partial) {
        return Class.forName(resolveClassName(partial));
    }


    @NotNull
    default MethodHandle resolveFieldSetter(@NotNull String clazzName, @NotNull String fieldName) {
        return resolveFieldSetter(resolveClass(clazzName), fieldName);
    }

    @SneakyThrows
    @NotNull
    default MethodHandle resolveFieldSetter(@NotNull Class<?> clazz, @NotNull String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return MethodHandles.lookup().unreflectSetter(field);
    }


    @NotNull
    default MethodHandle resolveFieldGetter(@NotNull String clazzName, @NotNull String fieldName) {
        return resolveFieldGetter(resolveClass(clazzName), fieldName);
    }

    @SneakyThrows
    @NotNull
    default MethodHandle resolveFieldGetter(@NotNull Class<?> clazz, @NotNull String fieldName) {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return MethodHandles.lookup().unreflectGetter(field);
    }


    @NotNull
    default MethodHandle resolveMethod(@NotNull String clazzName, @NotNull String fieldName) {
        return resolveMethod(resolveClass(clazzName), fieldName);
    }

    @SneakyThrows
    @NotNull
    default MethodHandle resolveMethod(@NotNull Class<?> clazz, @NotNull String fieldName) {
        Method method = clazz.getDeclaredMethod(fieldName);
        method.setAccessible(true);
        return MethodHandles.lookup().unreflect(method);
    }
}

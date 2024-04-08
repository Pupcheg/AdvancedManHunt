package me.supcheg.advancedmanhunt.bridge.impl.nms;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectiveAccessor {

    @SneakyThrows
    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static MethodHandle resolveCraftBukkitMethod(@NotNull String className, @NotNull String methodName,
                                                        @NotNull Class<?>... parameters) {
        Class<?> clazz = Class.forName(Bukkit.getServer().getClass().getPackageName() + "." + className);
        Method method = clazz.getMethod(methodName, parameters);
        method.trySetAccessible();
        return MethodHandles.lookup().unreflect(method);
    }

    @SneakyThrows
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static MethodHandle resolveCraftBukkitFieldSetter(@NotNull String className, @NotNull String fieldName) {
        Class<?> clazz = Class.forName(Bukkit.getServer().getClass().getPackageName() + "." + className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return MethodHandles.lookup().unreflectSetter(field);
    }
}

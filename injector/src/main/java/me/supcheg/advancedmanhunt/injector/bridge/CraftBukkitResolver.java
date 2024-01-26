package me.supcheg.advancedmanhunt.injector.bridge;

import com.destroystokyo.paper.util.SneakyThrow;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class CraftBukkitResolver {
    private CraftBukkitResolver() {
    }

    @NotNull
    public static MethodHandle resolveMethodInClass(@NotNull String className,
                                                    @NotNull String methodName,
                                                    @NotNull Class<?>... parameters) {
        try {
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit.v1_20_R3." + className);
            Method method = clazz.getMethod(methodName, parameters);
            return MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
}

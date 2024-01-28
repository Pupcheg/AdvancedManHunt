package me.supcheg.advancedmanhunt.injector.bridge;

import com.destroystokyo.paper.util.SneakyThrow;
import com.google.common.base.Suppliers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftBukkitResolver {

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Supplier<MethodHandle> resolveMethodInClassLater(@NotNull String className,
                                                                   @NotNull String methodName,
                                                                   @NotNull Class<?>... parameters) {
        return Suppliers.memoize(() -> resolveMethodInClass(className, methodName, parameters));
    }

    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static MethodHandle resolveMethodInClass(@NotNull String className,
                                                    @NotNull String methodName,
                                                    @NotNull Class<?>... parameters) {
        try {
            Class<?> clazz = Class.forName(Bukkit.getServer().getClass().getPackageName() + "." + className);
            Method method = clazz.getMethod(methodName, parameters);
            return MethodHandles.lookup().unreflect(method);
        } catch (Exception e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }
}

package me.supcheg.advancedmanhunt.injector;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectiveAccessor {
    public static final MethodHandle craftPlayer_getHandle =
            resolveNms("entity.CraftPlayer", "getHandle");
    public static final MethodHandle craftInventory_getInventory =
            resolveNms("inventory.CraftInventory", "getInventory");
    public static final MethodHandle craftContainer_getNotchInventoryType =
            resolveNms("inventory.CraftContainer", "getNotchInventoryType", Inventory.class);

    @SneakyThrows
    @NotNull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static MethodHandle resolveNms(@NotNull String className, @NotNull String methodName,
                                          @NotNull Class<?>... parameters) {
        Class<?> clazz = Class.forName(Bukkit.getServer().getClass().getPackageName() + "." + className);
        Method method = clazz.getMethod(methodName, parameters);
        method.trySetAccessible();
        return MethodHandles.lookup().unreflect(method);
    }
}

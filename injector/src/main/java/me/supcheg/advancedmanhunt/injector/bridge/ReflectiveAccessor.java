package me.supcheg.advancedmanhunt.injector.bridge;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.Inventory;

import java.lang.invoke.MethodHandle;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectiveAccessor {
    public static final Supplier<MethodHandle> craftPlayer_getHandle =
            CraftBukkitResolver.resolveMethodInClassLater("entity.CraftPlayer", "getHandle");
    public static final Supplier<MethodHandle> craftInventory_getInventory =
            CraftBukkitResolver.resolveMethodInClassLater("inventory.CraftInventory", "getInventory");
    public static final Supplier<MethodHandle> craftContainer_getNotchInventoryType =
            CraftBukkitResolver.resolveMethodInClassLater("inventory.CraftContainer", "getNotchInventoryType", Inventory.class);
}

package me.supcheg.advancedmanhunt.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class UnsafeNMS {
    /**
     * @return {@code org.bukkit.craftbukkit.v1_20_R1} etd.
     */
    @NotNull
    public static String locateCraftBukkit() {
        String originalMinecraftVersion = Bukkit.getMinecraftVersion();
        String[] split = originalMinecraftVersion.split("\\.");
        return "org.bukkit.craftbukkit.v%s_%s_R1".formatted(split[0], split[1]);
    }
}

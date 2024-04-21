package me.supcheg.advancedmanhunt.reflect;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectAccessors {
    public static final ReflectAccessor NO_CHANGES =
            partial -> partial;
    public static final ReflectAccessor CRAFT_BUKKIT =
            partial -> Bukkit.getServer().getClass().getPackageName() + '.' + partial;
}

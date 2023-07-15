package me.supcheg.advancedmanhunt.command.util;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Objects;

public class MojangBrigadierInjector {

    @NotNull
    public static CommandDispatcher<BukkitBrigadierCommandSource> getGlobalDispatcher() {
        Object console = invokeField(Bukkit.getServer(), "console");
        Object vanillaCommandDispatcher = invokeField(console, "vanillaCommandDispatcher");
        Object dispatcher = invokeField(vanillaCommandDispatcher, "g");
        @SuppressWarnings("unchecked")
        CommandDispatcher<BukkitBrigadierCommandSource> casted = (CommandDispatcher<BukkitBrigadierCommandSource>) dispatcher;
        return casted;
    }

    @SneakyThrows
    @NotNull
    private static Object invokeField(@NotNull Object obj, @NotNull String fieldName) {
        Class<?> clazz = obj.getClass();

        Field field = null;
        do {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
            clazz = Objects.requireNonNull(clazz.getSuperclass(), "super of " + clazz);
        } while (field == null);

        field.setAccessible(true);
        return field.get(obj);
    }
}

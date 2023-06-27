package me.supcheg.advancedmanhunt.command.util;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.GameCommand;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class MojangBrigadierInjector {

    public static void injectCommands(@NotNull AdvancedManHuntPlugin plugin) {
        var globalDispatcher = initGlobalDispatcher();

        new GameCommand(plugin).register(globalDispatcher);
        new TemplateCommand(plugin).register(globalDispatcher);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static CommandDispatcher<BukkitBrigadierCommandSource> initGlobalDispatcher() {
        var console = invokeField(Bukkit.getServer(), "console");
        var vanillaCommandDispatcher = invokeField(console, "vanillaCommandDispatcher");
        var dispatcher = invokeField(vanillaCommandDispatcher, "g");
        return (CommandDispatcher<BukkitBrigadierCommandSource>) dispatcher;
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
            clazz = clazz.getSuperclass();
        } while (field == null);

        field.setAccessible(true);
        return field.get(obj);
    }
}

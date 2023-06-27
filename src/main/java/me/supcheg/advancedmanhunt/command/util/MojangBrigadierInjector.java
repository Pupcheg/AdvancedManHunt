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
        CommandDispatcher<BukkitBrigadierCommandSource> globalDispatcher = initGlobalDispatcher();

        new GameCommand(plugin).register(globalDispatcher);
        new TemplateCommand(plugin).register(globalDispatcher);
    }

    @NotNull
    public static CommandDispatcher<BukkitBrigadierCommandSource> initGlobalDispatcher() {
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
            clazz = clazz.getSuperclass();
        } while (field == null);

        field.setAccessible(true);
        return field.get(obj);
    }
}

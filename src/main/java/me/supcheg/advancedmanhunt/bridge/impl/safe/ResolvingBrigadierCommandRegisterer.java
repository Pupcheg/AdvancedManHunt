package me.supcheg.advancedmanhunt.bridge.impl.safe;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.util.Unchecked;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ResolvingBrigadierCommandRegisterer implements BrigadierCommandRegisterer {
    private final CommandDispatcher<BukkitBrigadierCommandSource> dispatcher = findCommandDispatcher();

    @SneakyThrows
    private static CommandDispatcher<BukkitBrigadierCommandSource> findCommandDispatcher() {
        Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
        Object minecraftServer = minecraftServerClass.getMethod("getServer").invoke(null);

        for (Field commandsField : minecraftServerClass.getFields()) {
            if (!commandsField.getType().getName().toLowerCase().contains("command")) {
                continue;
            }
            commandsField.setAccessible(true);
            Object commands = commandsField.get(minecraftServer);

            if (commands == null) {
                continue;
            }

            for (Field field : commands.getClass().getDeclaredFields()) {
                if (field.getType() != CommandDispatcher.class) {
                    continue;
                }
                field.setAccessible(true);
                return Unchecked.uncheckedCast(field.get(commands));
            }
        }

        throw new IllegalStateException("Unable to find CommandDispatcher");
    }

    @Override
    public void registerCommand(@NotNull LiteralArgumentBuilder<BukkitBrigadierCommandSource> command) {
        dispatcher.register(command);
    }
}

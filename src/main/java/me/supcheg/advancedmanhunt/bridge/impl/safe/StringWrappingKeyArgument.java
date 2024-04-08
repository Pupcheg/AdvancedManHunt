package me.supcheg.advancedmanhunt.bridge.impl.safe;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.KeyArgument;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class StringWrappingKeyArgument implements KeyArgument {
    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, StringArgumentType.string());
    }

    @SuppressWarnings("PatternValidation")
    @SneakyThrows
    @NotNull
    @Override
    public Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        String raw = StringArgumentType.getString(ctx, name);
        try {
            return Key.key(raw);
        } catch (Throwable thr) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Illegal key: " + raw)).create();
        }
    }
}

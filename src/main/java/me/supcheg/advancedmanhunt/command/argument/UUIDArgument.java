package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UUIDArgument {

    @NotNull
    @Contract(pure = true)
    public static RequiredArgumentBuilder<BukkitBrigadierCommandSource, String> uniqueId(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, StringArgumentType.string());
    }

    @NotNull
    public static UUID getUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        String raw = ctx.getArgument(name, String.class);
        try {
            return UUID.fromString(raw);
        } catch (Exception e) {
            throw CustomExceptions.INVALID_UNIQUE_ID.createWithContext(new StringReader(ctx.getInput()));
        }
    }
}

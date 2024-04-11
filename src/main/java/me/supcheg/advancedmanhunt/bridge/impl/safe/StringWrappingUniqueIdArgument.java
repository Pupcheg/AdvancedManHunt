package me.supcheg.advancedmanhunt.bridge.impl.safe;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StringWrappingUniqueIdArgument implements UniqueIdArgument {
    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> uniqueId(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, StringArgumentType.string());
    }

    @NotNull
    @Override
    public UUID getUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        String raw = StringArgumentType.getString(ctx, name);
        return UUID.fromString(raw);
    }
}

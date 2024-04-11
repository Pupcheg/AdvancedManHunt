package me.supcheg.advancedmanhunt.bridge.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public interface KeyArgument {
    @NotNull
    RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name);

    @NotNull
    Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name);
}

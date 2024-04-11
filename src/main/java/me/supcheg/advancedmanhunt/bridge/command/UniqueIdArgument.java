package me.supcheg.advancedmanhunt.bridge.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface UniqueIdArgument {
    @NotNull
    RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> uniqueId(@NotNull String name);

    @NotNull
    UUID getUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name);
}

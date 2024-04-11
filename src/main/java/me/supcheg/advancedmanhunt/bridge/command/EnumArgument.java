package me.supcheg.advancedmanhunt.bridge.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

public interface EnumArgument {
    @NotNull
    <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> enumArg(@NotNull String name,
                                                                                              @NotNull Class<E> enumType);

    @NotNull
    <E extends Enum<E>> E getEnum(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name,
                                  @NotNull Class<E> enumType) throws CommandSyntaxException;
}

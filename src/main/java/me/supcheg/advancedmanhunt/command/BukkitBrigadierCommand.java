package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BukkitBrigadierCommand {

    @NotNull
    @Contract("-> new")
    LiteralArgumentBuilder<BukkitBrigadierCommandSource> build();

    default void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(build());
    }

    default void append(@NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder) {
        argumentBuilder.then(build());
    }
}

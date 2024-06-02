package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface BrigadierCommand {

    @NotNull
    @Contract("-> new")
    LiteralArgumentBuilder<CommandSourceStack> build();

    default void register(@NotNull Commands commands) {
        commands.register(build().build());
    }

    default void appendTo(@NotNull ArgumentBuilder<CommandSourceStack, ?> argumentBuilder) {
        argumentBuilder.then(build());
    }
}

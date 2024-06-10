package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface BrigadierCommand {

    @NotNull
    @Contract("-> new")
    LiteralArgumentBuilder<CommandSourceStack> build();

    @NotNull
    default List<String> aliases() {
        return Collections.emptyList();
    }

    default void register(@NotNull Commands commands) {
        commands.register(build().build(), aliases());
    }

    default void appendTo(@NotNull ArgumentBuilder<CommandSourceStack, ?> argumentBuilder) {
        argumentBuilder.then(build());
    }
}

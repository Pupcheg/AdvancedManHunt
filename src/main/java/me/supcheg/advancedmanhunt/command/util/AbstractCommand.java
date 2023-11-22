package me.supcheg.advancedmanhunt.command.util;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractCommand {

    protected AbstractCommand() {
    }

    @NotNull
    public abstract LiteralArgumentBuilder<BukkitBrigadierCommandSource> build();

    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(build());
    }

    public void append(@NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder) {
        argumentBuilder.then(build());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static LiteralArgumentBuilder<BukkitBrigadierCommandSource> literal(@NotNull String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <T> RequiredArgumentBuilder<BukkitBrigadierCommandSource, T> argument(@NotNull String name, @NotNull ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static SuggestionProvider<BukkitBrigadierCommandSource> suggestion(int suggestion) {
        return suggestion(String.valueOf(suggestion));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static SuggestionProvider<BukkitBrigadierCommandSource> suggestion(@NotNull String suggestion) {
        String lowerCaseSuggestion = suggestion.toLowerCase();
        return (context, builder) -> {
            String input = context.getInput();
            String partial = input.substring(input.lastIndexOf(' ') + 1).toLowerCase();
            if (lowerCaseSuggestion.startsWith(partial)) {
                builder.suggest(lowerCaseSuggestion);
            }
            return builder.buildFuture();
        };
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static SuggestionProvider<BukkitBrigadierCommandSource> suggestIfStartsWith(@NotNull Supplier<Collection<String>> suggestionsFunction) {
        return suggestIfStartsWith(ctx -> suggestionsFunction.get());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static SuggestionProvider<BukkitBrigadierCommandSource> suggestIfStartsWith(@NotNull Function<CommandContext<BukkitBrigadierCommandSource>, Collection<String>> suggestionsFunction) {
        return (context, builder) -> {
            String input = context.getInput();
            String partial = input.substring(input.lastIndexOf(' ') + 1);

            Collection<String> rawSuggestions = suggestionsFunction.apply(context);

            for (String suggestion : rawSuggestions) {
                if (suggestion.startsWith(partial)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
    }
}

package me.supcheg.advancedmanhunt.command.util;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.function.Function;

public abstract class AbstractCommand {
    protected final AdvancedManHuntPlugin plugin;

    protected AbstractCommand(@NotNull AdvancedManHuntPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher);

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

    @UnknownNullability
    public static <T> T getOrDefault(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx,
                                     @NotNull String name, @NotNull Class<T> clazz, @Nullable T defaultValue) {
        try {
            return ctx.getArgument(name, clazz);
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
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

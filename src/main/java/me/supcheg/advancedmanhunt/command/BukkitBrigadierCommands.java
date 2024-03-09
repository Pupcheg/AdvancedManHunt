package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitBrigadierCommands {
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

    @NotNull
    public static CommandSender getSender(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        return ctx.getSource().getBukkitSender();
    }

    @NotNull
    public static Player getPlayer(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        return Objects.requireNonNull((Player) ctx.getSource().getBukkitEntity(), "player");
    }

    @Nullable
    public static UUID tryGetSenderUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        Entity bukkitEntity = ctx.getSource().getBukkitEntity();
        return bukkitEntity == null ? null : bukkitEntity.getUniqueId();
    }
}

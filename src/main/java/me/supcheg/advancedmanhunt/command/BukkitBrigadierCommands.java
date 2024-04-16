package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.config.IntLimit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public static SuggestionProvider<BukkitBrigadierCommandSource> suggestIfStartsWith(@NotNull Iterable<String> suggestions) {
        return (context, builder) -> {
            String input = context.getInput();
            String partial = input.substring(input.lastIndexOf(' ') + 1);

            for (String suggestion : suggestions) {
                if (suggestion.startsWith(partial)) {
                    builder.suggest(suggestion);
                }
            }

            return builder.buildFuture();
        };
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <E extends Enum<E>> SuggestionProvider<BukkitBrigadierCommandSource> suggestEnumConstants(
            @NotNull Class<E> type) {
        E[] enumConstants = type.getEnumConstants();
        List<String> serializedConstants = new ArrayList<>(enumConstants.length);
        for (E enumConstant : enumConstants) {
            serializedConstants.add(enumConstant.toString().toLowerCase());
        }

        return suggestIfStartsWith(serializedConstants);
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

    @NotNull
    public static IntegerArgumentType asIntArgument(@NotNull IntLimit limit) {
        return IntegerArgumentType.integer(limit.getMinValue(), limit.getMaxValue());
    }
}

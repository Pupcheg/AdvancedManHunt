package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PathArgument {

    private static final SuggestionProvider<BukkitBrigadierCommandSource> SUGGESTION_PROVIDER = (ctx, builder) -> {
        String remaining = builder.getRemaining();
        if (remaining.isEmpty() || remaining.charAt(remaining.length() - 1) != '/') {
            builder.suggest(remaining + '/');
        }
        return builder.buildFuture();
    };

    @NotNull
    @Contract(pure = true)
    public static RequiredArgumentBuilder<BukkitBrigadierCommandSource, String> path(@NotNull String name) {
        return RequiredArgumentBuilder.<BukkitBrigadierCommandSource, String>argument(name, StringArgumentType.string())
                .suggests(SUGGESTION_PROVIDER);
    }

    @NotNull
    public static Path parsePath(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        String raw = ctx.getArgument(name, String.class).toUpperCase();
        try {
            return Path.of(raw);
        } catch (Exception e) {
            throw CustomExceptions.INVALID_PATH.createWithContext(new StringReader(ctx.getInput()));
        }
    }
}

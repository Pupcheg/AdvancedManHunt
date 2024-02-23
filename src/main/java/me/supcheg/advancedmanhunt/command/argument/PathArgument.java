package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PathArgument {

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
    public static Path getPath(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        String raw = StringArgumentType.getString(ctx, name);
        try {
            return Path.of(raw);
        } catch (Exception e) {
            throw CustomExceptions.INVALID_PATH.create();
        }
    }
}

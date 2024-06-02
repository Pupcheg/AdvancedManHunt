package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ManHuntGameArgumentType implements CustomArgumentType<ManHuntGame, UUID> {
    private static final SimpleCommandExceptionType NO_GAME = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.translatable("advancedmanhunt.exception.no_game"))
    );

    private final ManHuntGameService service;
    private final ArgumentType<UUID> uniqueIdArgumentType;

    private ManHuntGameArgumentType(@NotNull ManHuntGameService service) {
        this.service = service;
        this.uniqueIdArgumentType = ArgumentTypes.uuid();
    }

    @NotNull
    public static ArgumentType<ManHuntGame> manhuntGame(@NotNull ManHuntGameService service) {
        return new ManHuntGameArgumentType(service);
    }

    @NotNull
    public static ManHuntGame getManHuntGame(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) {
        return ctx.getArgument(name, ManHuntGame.class);
    }

    @NotNull
    @Override
    public ManHuntGame parse(@NotNull StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        UUID uniqueId = uniqueIdArgumentType.parse(reader);

        ManHuntGame game = service.getGame(uniqueId);
        if (game == null) {
            reader.setCursor(cursor);
            throw NO_GAME.create();
        }
        return game;
    }

    @NotNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                              @NotNull SuggestionsBuilder builder) {
        service.getStringKeys().forEach(builder::suggest);
        return builder.buildFuture();
    }

    @NotNull
    @Override
    public ArgumentType<UUID> getNativeType() {
        return uniqueIdArgumentType;
    }
}

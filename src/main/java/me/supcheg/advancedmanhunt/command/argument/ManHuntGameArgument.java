package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.brigadier.PaperBrigadier;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.suggestIfStartsWith;

public class ManHuntGameArgument {
    private static final SimpleCommandExceptionType NO_GAME = new SimpleCommandExceptionType(
            PaperBrigadier.message(Component.translatable("advancedmanhunt.exception.no_game"))
    );

    private final UniqueIdArgument uniqueIdArgument;
    private final ManHuntGameService service;
    private final SuggestionProvider<BukkitBrigadierCommandSource> gameIdsProvider;

    @Inject
    public ManHuntGameArgument(@NotNull ManHuntGameService service, @NotNull UniqueIdArgument uniqueIdArgument) {
        this.service = service;
        this.uniqueIdArgument = uniqueIdArgument;
        this.gameIdsProvider = suggestIfStartsWith(service.getStringKeys());
    }

    @NotNull
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> manhuntGame(@NotNull String name) {
        return uniqueIdArgument.uniqueId(name).suggests(gameIdsProvider);
    }

    @NotNull
    public ManHuntGame getManHuntGame(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        ManHuntGame game = service.getGame(uniqueIdArgument.getUniqueId(ctx, name));
        if (game == null) {
            throw NO_GAME.create();
        }
        return game;
    }
}

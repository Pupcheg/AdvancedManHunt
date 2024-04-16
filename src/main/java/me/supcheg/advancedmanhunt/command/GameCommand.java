package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.template.TemplateService;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.argument;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.asIntArgument;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getPlayer;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getSender;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.literal;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.suggestIfStartsWith;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameCommand implements BukkitBrigadierCommand {
    private static final String UNIQUE_ID = "unique_id";
    private static final String VALUE = "value";
    private static final String ENVIRONMENT = "environment";
    private static final String KEY = "key";
    private static final String ROLE = "role";

    private final TemplateService templateService;
    private final ManHuntGameService gameService;
    private final AdvancedGuiController guiController;

    private final KeyArgument keyArgument;
    private final UniqueIdArgument uniqueIdArgument;
    private final EnumArgument enumArgument;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("game")
                .then(literal("start")
                        .then(uniqueIdArgument.uniqueId(UNIQUE_ID)
                                .suggests(suggestIfStartsWith(gameService::getGameStringKeys))
                                .executes(this::start)
                        )
                )
                .then(literal("create")
                        .executes(this::create)
                )
                .then(literal("config")
                        .then(uniqueIdArgument.uniqueId(UNIQUE_ID)
                                .suggests(suggestIfStartsWith(gameService::getGameStringKeys))
                                .then(literal("randomize_roles")
                                        .then(argument(VALUE, bool())
                                                .executes(this::randomizeRoles)
                                        )
                                )
                                .then(literal("template")
                                        .then(enumArgument.enumArg(ENVIRONMENT, RealEnvironment.class)
                                                .then(keyArgument.key(KEY)
                                                        .executes(this::template)
                                                )
                                        )
                                )
                                .then(literal("hunters")
                                        .then(argument(VALUE, asIntArgument(config().game.configLimits.maxHunters))
                                                .executes(this::maxHunters)
                                        )
                                )
                                .then(literal("spectators")
                                        .then(argument(VALUE, asIntArgument(config().game.configLimits.maxSpectators))
                                                .executes(this::maxSpectators)
                                        )
                                )
                        )
                )
                .then(literal("join")
                        .then(uniqueIdArgument.uniqueId(UNIQUE_ID)
                                .suggests(suggestIfStartsWith(gameService::getGameStringKeys))
                                .executes(this::joinAnyRole)
                                .then(enumArgument.enumArg(ROLE, ManHuntRole.class)
                                        .executes(this::joinExpectedRole))
                        )
                )
                .then(literal("menu")
                        .executes(this::menu)
                );
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int start(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntGame game = gameService.getGame(gameUniqueId);
        gameService.assertCanConfigure(sender, game);

        gameService.start(game);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int menu(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        Player player = (Player) ctx.getSource().getBukkitSender();
        guiController.getGuiOrThrow(GamesListGui.KEY).open(player);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxSpectators(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntGame game = gameService.getGame(gameUniqueId);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .setMaxSpectators(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxHunters(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntGame game = gameService.getGame(gameUniqueId);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .setMaxHunters(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int template(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntGame game = gameService.getGame(gameUniqueId);
        gameService.assertCanConfigure(sender, game);

        RealEnvironment environment = enumArgument.getEnum(ctx, ENVIRONMENT, RealEnvironment.class);

        Key key = keyArgument.getKey(ctx, KEY);
        templateService.getTemplate(key);

        game.getConfig()
                .setTemplate(environment, key);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int randomizeRoles(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntGame game = gameService.getGame(gameUniqueId);
        gameService.assertCanConfigure(sender, game);

        boolean value = getBool(ctx, VALUE);

        game.getConfig()
                .setRandomizeRolesOnStart(value);

        return Command.SINGLE_SUCCESS;
    }

    private int joinExpectedRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);
        ManHuntRole role = enumArgument.getEnum(ctx, ROLE, ManHuntRole.class);

        ManHuntGame game = gameService.getGame(gameUniqueId);

        return game.addMember(player, role) ? Command.SINGLE_SUCCESS : 0;
    }

    private int joinAnyRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        UUID gameUniqueId = uniqueIdArgument.getUniqueId(ctx, UNIQUE_ID);

        ManHuntGame game = gameService.getGame(gameUniqueId);

        return game.addMember(player) != null ? Command.SINGLE_SUCCESS : 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int create(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        UUID owner = getPlayer(ctx).getUniqueId();
        gameService.createGame(owner);
        return Command.SINGLE_SUCCESS;
    }
}

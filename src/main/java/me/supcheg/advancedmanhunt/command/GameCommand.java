package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.command.argument.ManHuntGameArgument;
import me.supcheg.advancedmanhunt.command.argument.ManHuntRoleArgument;
import me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgument;
import me.supcheg.advancedmanhunt.command.argument.TemplateArgument;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.template.Template;
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
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class GameCommand implements BukkitBrigadierCommand {
    private static final String GAME = "game";
    private static final String VALUE = "value";
    private static final String ENVIRONMENT = "environment";
    private static final String TEMPLATE = "template";
    private static final String ROLE = "role";

    private final ManHuntGameService gameService;
    private final AdvancedGuiController guiController;

    private final ManHuntGameArgument manHuntGameArgument;
    private final TemplateArgument templateArgument;
    private final RealEnvironmentArgument environmentArgument;
    private final ManHuntRoleArgument roleArgument;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("game")
                .then(literal("start")
                        .then(manHuntGameArgument.manhuntGame(GAME)
                                .executes(this::start)
                        )
                )
                .then(literal("create")
                        .executes(this::create)
                )
                .then(literal("config")
                        .then(manHuntGameArgument.manhuntGame(GAME)
                                .then(literal("randomize_roles")
                                        .then(argument(VALUE, bool())
                                                .executes(this::randomizeRoles)
                                        )
                                )
                                .then(literal("template")
                                        .then(environmentArgument.environment(ENVIRONMENT)
                                                .then(templateArgument.template(TEMPLATE)
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
                        .then(manHuntGameArgument.manhuntGame(GAME)
                                .executes(this::joinAnyRole)
                                .then(roleArgument.role(ROLE)
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

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
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

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .setMaxSpectators(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxHunters(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .setMaxHunters(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int template(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        RealEnvironment environment = environmentArgument.getEnvironment(ctx, ENVIRONMENT);

        Template template = templateArgument.getTemplate(ctx, TEMPLATE);

        game.getConfig()
                .setTemplate(environment, template.getKey());

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int randomizeRoles(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        boolean value = getBool(ctx, VALUE);

        game.getConfig()
                .setRandomizeRolesOnStart(value);

        return Command.SINGLE_SUCCESS;
    }

    private int joinExpectedRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);
        ManHuntRole role = roleArgument.getRole(ctx, ROLE);

        return game.addMember(player, role) ? Command.SINGLE_SUCCESS : 0;
    }

    private int joinAnyRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        ManHuntGame game = manHuntGameArgument.getManHuntGame(ctx, GAME);

        return game.addMember(player) != null ? Command.SINGLE_SUCCESS : 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int create(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        UUID owner = getPlayer(ctx).getUniqueId();
        gameService.createGame(owner);
        return Command.SINGLE_SUCCESS;
    }
}

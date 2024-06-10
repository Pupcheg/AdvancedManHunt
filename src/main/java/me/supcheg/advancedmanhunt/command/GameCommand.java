package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.gui.ManHuntGamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.asIntArgument;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getPlayer;
import static me.supcheg.advancedmanhunt.command.argument.ManHuntGameArgumentType.getManHuntGame;
import static me.supcheg.advancedmanhunt.command.argument.ManHuntGameArgumentType.manhuntGame;
import static me.supcheg.advancedmanhunt.command.argument.ManHuntRoleArgumentType.getManhuntRole;
import static me.supcheg.advancedmanhunt.command.argument.ManHuntRoleArgumentType.manhuntRole;
import static me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgumentType.environment;
import static me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgumentType.getEnvironment;
import static me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType.getTemplate;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class GameCommand implements BrigadierCommand {
    private static final String GAME = "game";
    private static final String VALUE = "value";
    private static final String ENVIRONMENT = "environment";
    private static final String TEMPLATE = "template";
    private static final String ROLE = "role";

    private final ManHuntGameService gameService;
    private final AdvancedGuiController guiController;
    private final TemplateService templateService;

    @NotNull
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("game")
                .then(literal("start")
                        .then(argument(GAME, manhuntGame(gameService))
                                .executes(this::start)
                        )
                )
                .then(literal("create")
                        .executes(this::create)
                )
                .then(literal("config")
                        .then(argument(GAME, manhuntGame(gameService))
                                .then(literal("randomize_roles")
                                        .then(argument(VALUE, bool())
                                                .executes(this::randomizeRoles)
                                        )
                                )
                                .then(literal("template")
                                        .then(argument(ENVIRONMENT, environment())
                                                .then(argument(TEMPLATE, TemplateArgumentType.template(templateService))
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
                        .then(argument(GAME, manhuntGame(gameService))
                                .executes(this::joinAnyRole)
                                .then(argument(ROLE, manhuntRole())
                                        .executes(this::joinExpectedRole))
                        )
                )
                .then(literal("menu")
                        .executes(this::menu)
                );
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int start(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        gameService.start(game);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int menu(@NotNull CommandContext<CommandSourceStack> ctx) {
        Player player = getPlayer(ctx);
        guiController.getGuiOrThrow(ManHuntGamesListGui.KEY).open(player);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxSpectators(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .maxSpectators(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxHunters(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        int value = getInteger(ctx, VALUE);

        game.getConfig()
                .maxHunters(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int template(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        RealEnvironment environment = getEnvironment(ctx, ENVIRONMENT);

        Template template = getTemplate(ctx, TEMPLATE);

        game.getConfig()
                .template(environment, template.getKey());

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int randomizeRoles(@NotNull CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        gameService.assertCanConfigure(sender, game);

        boolean value = getBool(ctx, VALUE);

        game.getConfig()
                .randomizeRolesOnStart(value);

        return Command.SINGLE_SUCCESS;
    }

    private int joinExpectedRole(@NotNull CommandContext<CommandSourceStack> ctx) {
        UUID player = getPlayer(ctx).getUniqueId();

        ManHuntGame game = getManHuntGame(ctx, GAME);
        ManHuntRole role = getManhuntRole(ctx, ROLE);

        return game.addMember(player, role) ? Command.SINGLE_SUCCESS : 0;
    }

    private int joinAnyRole(@NotNull CommandContext<CommandSourceStack> ctx) {
        UUID player = getPlayer(ctx).getUniqueId();

        ManHuntGame game = getManHuntGame(ctx, GAME);

        return game.addMember(player) != null ? Command.SINGLE_SUCCESS : 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int create(@NotNull CommandContext<CommandSourceStack> ctx) {
        UUID owner = getPlayer(ctx).getUniqueId();
        gameService.createGame(owner);
        return Command.SINGLE_SUCCESS;
    }
}

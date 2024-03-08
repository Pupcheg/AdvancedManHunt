package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.argument;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getPlayer;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getSender;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.literal;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.enumArg;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.getEnum;
import static me.supcheg.advancedmanhunt.command.argument.UUIDArgument.getUniqueId;
import static me.supcheg.advancedmanhunt.command.argument.UUIDArgument.uniqueId;
import static me.supcheg.advancedmanhunt.command.exception.CommandAssertions.assertCanConfigure;
import static me.supcheg.advancedmanhunt.command.exception.CommandAssertions.requireNonNull;

@CustomLog
@RequiredArgsConstructor
public class GameCommand implements BukkitBrigadierCommand {
    private final EntityRepository<Template, String> templateRepository;
    private final ManHuntGameRepository gameRepository;
    private final AdvancedGuiController guiController;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("game")
                .then(literal("create")
                        .executes(this::create)
                )
                .then(literal("config")
                        .then(uniqueId("uid")
                                .then(literal("randomize_roles")
                                        .then(argument("value", bool())
                                                .executes(this::randomizeRoles)
                                        )
                                )
                                .then(literal("template")
                                        .then(enumArg("environment", World.Environment.class)
                                                .then(argument("key", string())
                                                        .executes(this::template)
                                                )
                                        )
                                )
                                .then(literal("hunters")
                                        .then(argument("value", integer(1, 5))
                                                .executes(this::maxHunters)
                                        )
                                )
                                .then(literal("spectators")
                                        .then(argument("value", integer(0, 15))
                                                .executes(this::maxSpectators)
                                        )
                                )
                        )
                )
                .then(literal("join")
                        .then(uniqueId("uid")
                                .executes(this::joinAnyRole)
                                .then(enumArg("role", ManHuntRole.class)
                                        .executes(this::joinExpectedRole))
                        )
                )
                .then(literal("menu")
                        .executes(this::menu)
                );
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

        UUID gameUniqueId = getUniqueId(ctx, "uid");
        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");
        assertCanConfigure(sender, game);

        int value = getInteger(ctx, "value");

        game.getConfig()
                .setMaxSpectators(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int maxHunters(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = getUniqueId(ctx, "uid");
        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");
        assertCanConfigure(sender, game);

        int value = getInteger(ctx, "value");

        game.getConfig()
                .setMaxHunters(value);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int template(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = getUniqueId(ctx, "uid");
        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");
        assertCanConfigure(sender, game);

        World.Environment environment = getEnum(ctx, "environment", World.Environment.class);

        String key = getString(ctx, "environment");
        Template template = templateRepository.getEntity(key);
        requireNonNull(template, "template");

        game.getConfig()
                .setTemplate(environment, key);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int randomizeRoles(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = getSender(ctx);

        UUID gameUniqueId = getUniqueId(ctx, "uid");
        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");
        assertCanConfigure(sender, game);

        boolean value = getBool(ctx, "value");

        game.getConfig()
                .setRandomizeRolesOnStart(value);

        return Command.SINGLE_SUCCESS;
    }

    private int joinExpectedRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        UUID gameUniqueId = getUniqueId(ctx, "uid");
        ManHuntRole role = getEnum(ctx, "role", ManHuntRole.class);

        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");

        return game.addMember(player, role) ? Command.SINGLE_SUCCESS : 0;
    }

    private int joinAnyRole(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        UUID player = getPlayer(ctx).getUniqueId();

        UUID gameUniqueId = getUniqueId(ctx, "uid");

        ManHuntGame game = gameRepository.getEntity(gameUniqueId);
        requireNonNull(game, "game");

        return game.addMember(player) != null ? Command.SINGLE_SUCCESS : 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int create(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        UUID owner = getPlayer(ctx).getUniqueId();
        gameRepository.create(owner);
        return Command.SINGLE_SUCCESS;
    }
}

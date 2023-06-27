package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameCommand extends AbstractCommand {

    public GameCommand(@NotNull AdvancedManHuntPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(
                literal("game")
                        .then(literal("fast").executes(this::fast))
        );
    }

    private int fast(@NotNull CommandContext<BukkitBrigadierCommandSource> commandSource) throws CommandSyntaxException {
        int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
        if (onlinePlayersCount < 2) {
            throw new SimpleCommandExceptionType(new LiteralMessage("Expected 2 players, online: " + onlinePlayersCount)).create();
        }

        var players = Bukkit.getOnlinePlayers().iterator();
        var player1 = players.next();
        var player2 = players.next();


        var template = plugin.getTemplateRepository().getTemplates().iterator().next();
        Objects.requireNonNull(template);

        var game = plugin.getGameRepository().create(
                plugin.getPlayerViewRepository().get(player1),
                5, 5
        );

        var view1 = plugin.getPlayerViewRepository().get(player1);
        var view2 = plugin.getPlayerViewRepository().get(player2);

        game.addPlayer(view1);
        game.addPlayer(view2);

        game.start(
                ManHuntGameConfiguration.builder()
                        .randomizeRolesOnStart(true)
                        .overworldTemplate(template)
                        .netherTemplate(template)
                        .endTemplate(template)
                        .spawnLocationFinder(new LazySpawnLocationFinder(
                                ThreadLocalRandom.current(),
                                new Vector(2, 2, 2),
                                new Vector(5, 5, 5),
                                Distance.ofBlocks(50)
                        ))
                        .build()
        );
        return Command.SINGLE_SUCCESS;
    }
}

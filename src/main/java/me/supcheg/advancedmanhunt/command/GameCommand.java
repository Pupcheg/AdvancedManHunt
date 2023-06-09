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
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
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

        Iterator<? extends Player> players = Bukkit.getOnlinePlayers().iterator();
        Player player1 = players.next();
        Player player2 = players.next();


        Template template = plugin.getTemplateRepository().getTemplates().iterator().next();
        Objects.requireNonNull(template);

        ManHuntGame game = plugin.getGameRepository().create(
                plugin.getPlayerViewRepository().get(player1),
                5, 5
        );

        ManHuntPlayerView view1 = plugin.getPlayerViewRepository().get(player1);
        ManHuntPlayerView view2 = plugin.getPlayerViewRepository().get(player2);

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

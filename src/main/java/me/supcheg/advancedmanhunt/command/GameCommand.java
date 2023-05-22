package me.supcheg.advancedmanhunt.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("game")
@AllArgsConstructor
public class GameCommand extends BaseCommand {

    private final AdvancedManHuntPlugin plugin;

    @Subcommand("fast")
    public void fast(@NotNull CommandSender commandSender) {

        var players = Bukkit.getOnlinePlayers().iterator();
        var player1 = players.next();
        var player2 = players.next();


        var template = plugin.getTemplateRepository().getTemplates().get(0);
        Objects.requireNonNull(template);

        var game = plugin.getGameRepository().create(
                plugin.getPlayerViewRepository().get(player1),
                5, 5
        );

        var view1 = plugin.getPlayerViewRepository().get(player1);
        var view2 = plugin.getPlayerViewRepository().get(player2);

        commandSender.sendPlainMessage("""
                           %s's role: %s
                           %s's role: %s
                        """.formatted(
                        player1.getName(), view1.getRole(),
                        player2.getName(), view2.getRole()
                )
        );

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

    }
}

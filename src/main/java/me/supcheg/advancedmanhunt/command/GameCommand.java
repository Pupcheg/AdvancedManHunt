package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.region.impl.LazySpawnLocationFinder;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class GameCommand extends AbstractCommand {
    private final TemplateRepository templateRepository;
    private final ManHuntGameRepository gameRepository;

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


        Template overworldTemplate = templateRepository.getTemplate("template_1");
        Template netherTemplate = templateRepository.getTemplate("template_1_nether");
        Template endTemplate = templateRepository.getTemplate("template_1_the_end");

        Objects.requireNonNull(overworldTemplate);
        Objects.requireNonNull(netherTemplate);
        Objects.requireNonNull(endTemplate);

        ManHuntGame game = gameRepository.create(player1.getUniqueId(), 5, 5);

        game.addMember(player1.getUniqueId());
        game.addMember(player2.getUniqueId());

        game.start(
                ManHuntGameConfiguration.builder()
                        .randomizeRolesOnStart(true)
                        .overworldTemplate(overworldTemplate)
                        .netherTemplate(netherTemplate)
                        .endTemplate(endTemplate)
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

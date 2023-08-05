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
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

@AllArgsConstructor
public class GameCommand extends AbstractCommand {
    private final EntityRepository<Template, String> templateRepository;
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


        Template overworldTemplate = templateRepository.getEntity("template_1");
        Template netherTemplate = templateRepository.getEntity("template_1_nether");
        Template endTemplate = templateRepository.getEntity("template_1_the_end");

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
                        .spawnLocationFinder(CachedSpawnLocationFinder.randomFrom(overworldTemplate.getSpawnLocations()))
                        .build()
        );
        return Command.SINGLE_SUCCESS;
    }
}

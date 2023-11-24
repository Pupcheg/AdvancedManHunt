package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DebugCommand extends AbstractCommand {
    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("debug")
                .then(literal("fast_game").executes(this::fastGame));
    }

    public void appendIfEnabled(@NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder) {
        if (AdvancedManHuntConfig.ENABLE_DEBUG) {
            append(argumentBuilder);
        }
    }

    private int fastGame(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        AdvancedManHuntPlugin plugin = getPlugin();

        var onlinePlayers = Bukkit.getOnlinePlayers().iterator();
        UUID player1 = onlinePlayers.next().getUniqueId();
        UUID player2 = onlinePlayers.next().getUniqueId();

        ManHuntGame game = plugin.getGameRepository().create(player1);
        game.addMember(player1);
        game.addMember(player2);
        game.start();

        return Command.SINGLE_SUCCESS;
    }

    @NotNull
    private static AdvancedManHuntPlugin getPlugin() {
        return (AdvancedManHuntPlugin) Bukkit.getPluginManager().getPlugin(AdvancedManHuntPlugin.NAME);
    }
}

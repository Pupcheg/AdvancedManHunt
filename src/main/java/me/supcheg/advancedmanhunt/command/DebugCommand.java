package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.CustomLog;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

@CustomLog
public class DebugCommand extends AbstractCommand {
    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("debug")
                .requires(src -> src.getBukkitSender().hasPermission(Permission.DEBUG))
                .then(literal("fast_game").executes(this::fastGame))
                .then(literal("load_template").executes(this::loadTemplate))
                .then(literal("open_gui")
                        .then(argument("key", greedyString())
                                .suggests(suggestIfStartsWith(() -> getPlugin()
                                        .getGuiController()
                                        .getRegisteredKeys()
                                )).executes(this::openGui)
                        )
                );
    }

    public void appendIfEnabled(@NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder) {
        if (AdvancedManHuntConfig.get().debug) {
            append(argumentBuilder);
        }
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int openGui(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        try {
            String key = getString(ctx, "key");
            AdvancedManHuntPlugin plugin = getPlugin();

            plugin.getGuiController().getGuiOrThrow(key).open(getPlayer(ctx));
        } catch (Exception e) {
            log.error("", e);
        }
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int loadTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        try {
            AdvancedManHuntPlugin plugin = getPlugin();

            Template template = plugin.getTemplateRepository().getEntity(AdvancedManHuntConfig.get().game.configDefaults.overworldTemplate);
            Objects.requireNonNull(template, "template");

            WorldReference reference = WorldReference.of("amh_rw-3");
            GameRegion region = new GameRegion(reference, KeyedCoord.of(32), KeyedCoord.of(64));

            plugin.getTemplateLoader().loadTemplate(region, template).join();

            Location center = region.getCenterBlock().asLocation(reference.getWorld(), 80);

            getPlayer(ctx).teleport(center);
        } catch (Exception e) {
            log.error("", e);
        }

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int fastGame(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        AdvancedManHuntPlugin plugin = getPlugin();

        Iterator<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers().iterator();
        UUID player1 = onlinePlayers.next().getUniqueId();
        UUID player2 = onlinePlayers.next().getUniqueId();

        ManHuntGame game = plugin.getGameRepository().create(player1);
        game.addMember(player1);
        game.addMember(player2);
        game.start();

        return Command.SINGLE_SUCCESS;
    }

    @NotNull
    private static Player getPlayer(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        return Objects.requireNonNull((Player) ctx.getSource().getBukkitEntity(), "player");
    }

    @NotNull
    private static AdvancedManHuntPlugin getPlugin() {
        return Objects.requireNonNull((AdvancedManHuntPlugin) Bukkit.getPluginManager().getPlugin(AdvancedManHuntPlugin.NAME), "plugin");
    }
}

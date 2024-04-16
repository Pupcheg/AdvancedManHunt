package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getPlayer;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.literal;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.suggestIfStartsWith;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DebugCommand implements BukkitBrigadierCommand {
    private static final String KEY = "key";

    private final AdvancedGuiController guiController;
    private final TemplateService templateService;
    private final ManHuntGameService gameService;
    private final KeyArgument keyArgument;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("debug")
                .requires(src -> src.getBukkitSender().hasPermission(Permission.DEBUG))
                .then(literal("fast_game").executes(this::fastGame))
                .then(literal("load_template").executes(this::loadTemplate))
                .then(literal("open_gui")
                        .then(keyArgument.key(KEY)
                                .suggests(suggestIfStartsWith(() ->
                                        guiController.getRegisteredKeys().stream().map(Key::asString)::iterator
                                ))
                                .executes(this::openGui)
                        )
                );
    }

    @Override
    public void appendTo(@NotNull ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder) {
        if (config().debug) {
            BukkitBrigadierCommand.super.appendTo(argumentBuilder);
        }
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int openGui(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        try {
            Key key = keyArgument.getKey(ctx, KEY);
            guiController.getGuiOrThrow(key).open(getPlayer(ctx));
        } catch (Throwable thr) {
            log.error("", thr);
        }
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int loadTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        try {
            Template template = templateService.getTemplate(config().game.configDefaults.overworldTemplate);
            Objects.requireNonNull(template, "template");

            WorldReference reference = WorldReference.of("amh_rw-3");
            GameRegion region = new GameRegion(reference, Coord.coordSameXZ(32), Coord.coordSameXZ(64));

            templateService.loadTemplate(region, template).join();

            Location center = region.getCenterBlock().asLocation(reference.getWorld(), 80);

            getPlayer(ctx).teleport(center);
        } catch (Throwable thr) {
            log.error("", thr);
        }

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int fastGame(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        try {
            Iterator<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers().iterator();
            UUID player1 = onlinePlayers.next().getUniqueId();
            UUID player2 = onlinePlayers.next().getUniqueId();

            ManHuntGame game = gameService.createGame(player1);
            game.addMember(player1);
            game.addMember(player2);
            gameService.start(game);
        } catch (Throwable thr) {
            log.error("", thr);
        }
        return Command.SINGLE_SUCCESS;
    }
}

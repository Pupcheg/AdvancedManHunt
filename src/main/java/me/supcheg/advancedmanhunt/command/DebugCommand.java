package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameService;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
import me.supcheg.advancedmanhunt.player.Permission;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Iterator;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getPlayer;
import static me.supcheg.advancedmanhunt.command.argument.AdvancedGuiArgumentType.advancedGui;
import static me.supcheg.advancedmanhunt.command.argument.AdvancedGuiArgumentType.getAdvancedGui;
import static me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType.getTemplate;
import static me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType.template;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DebugCommand implements BrigadierCommand {
    private static final String KEY = "key";

    private final TemplateService templateService;
    private final ManHuntGameService gameService;
    private final AdvancedGuiController guiController;

    @NotNull
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("debug")
                .requires(src -> src.getSender().hasPermission(Permission.DEBUG))
                .then(literal("fast_game").executes(this::fastGame))
                .then(literal("load_template")
                        .then(argument(KEY, template(templateService))
                                .executes(this::loadTemplate)
                        )
                )
                .then(literal("open_gui")
                        .then(argument(KEY, advancedGui(guiController))
                                .executes(this::openGui)
                        )
                );
    }

    @Override
    public void appendTo(@NotNull ArgumentBuilder<CommandSourceStack, ?> argumentBuilder) {
        if (config().debug) {
            BrigadierCommand.super.appendTo(argumentBuilder);
        }
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int openGui(@NotNull CommandContext<CommandSourceStack> ctx) {
        getAdvancedGui(ctx, KEY).open(getPlayer(ctx));
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int loadTemplate(@NotNull CommandContext<CommandSourceStack> ctx) {
        Template template = getTemplate(ctx, KEY);

        WorldReference reference = WorldReference.of("amh_rw-3");
        GameRegion region = new GameRegion(reference, DistancePair.ofRegions(32, 32), DistancePair.ofRegions(65, 65).subtractBlocks(1, 1));

        templateService.loadTemplate(region, template).join();

        Location center = region.positionSource().offset().toLocation(reference.getWorld()).add(0, 80, 0);
        getPlayer(ctx).teleport(center);

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int fastGame(@NotNull CommandContext<CommandSourceStack> ctx) {
        Iterator<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers().iterator();
        UUID player1 = onlinePlayers.next().getUniqueId();
        UUID player2 = onlinePlayers.next().getUniqueId();

        ManHuntGame game = gameService.createGame(player1);
        game.addMember(player1, ManHuntRole.RUNNER);
        game.addMember(player2, ManHuntRole.HUNTER);
        gameService.start(game);

        return Command.SINGLE_SUCCESS;
    }
}

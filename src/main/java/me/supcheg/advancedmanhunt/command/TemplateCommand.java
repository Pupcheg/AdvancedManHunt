package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.enumArg;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.parseEnum;

public class TemplateCommand extends AbstractCommand {
    public TemplateCommand(@NotNull AdvancedManHuntPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(
                literal("template")
                        .then(literal("generate")
                                .then(argument("name", string())
                                        .then(argument("side_size", integer(0))
                                                .then(enumArg("environment", World.Environment.class)
                                                        .executes(this::generate)
                                                        .then(argument("seed", longArg(0))
                                                                .suggests(suggestion("0"))
                                                                .executes(this::generate)
                                                                .then(argument("spawn_locations_count", integer(0))
                                                                        .suggests(suggestion("16"))
                                                                        .executes(this::generate)
                                                                        .then(argument("hunters_per_location", integer(1))
                                                                                .suggests(suggestion("5"))
                                                                                .executes(this::generate)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }

    private int generate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getBukkitSender();
        TemplateCreateConfig config = TemplateCreateConfig.builder()
                .name(ctx.getArgument("name", String.class))
                .sideSize(Distance.ofRegions(ctx.getArgument("side_size", int.class)))
                .environment(parseEnum(ctx, "environment", World.Environment.class))

                .seed(getOrDefault(ctx, "seed", long.class, 0L))
                .spawnLocationsCount(getOrDefault(ctx, "spawn_locations_count", int.class, 16))
                .huntersPerLocationCount(getOrDefault(ctx, "hunters_per_location", int.class, 5))
                .build();

        plugin.getTemplateTaskFactory().runCreateTask(sender, config);
        return Command.SINGLE_SUCCESS;
    }
}

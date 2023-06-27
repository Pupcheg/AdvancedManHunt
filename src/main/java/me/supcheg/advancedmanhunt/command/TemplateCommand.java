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

    private static final String NAME = "name";
    private static final String SIDE_SIZE = "side_size";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";

    public TemplateCommand(@NotNull AdvancedManHuntPlugin plugin) {
        super(plugin);
    }

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(
                literal("template")
                        .then(literal("generate")
                                .then(argument(NAME, string())
                                        .then(argument(SIDE_SIZE, integer(0))
                                                .then(enumArg(ENVIRONMENT, World.Environment.class)
                                                        .executes(this::generate)
                                                        .then(argument(SEED, longArg(0))
                                                                .suggests(suggestion("0"))
                                                                .executes(this::generate)
                                                                .then(argument(SPAWN_LOCATIONS_COUNT, integer(0))
                                                                        .suggests(suggestion("16"))
                                                                        .executes(this::generate)
                                                                        .then(argument(HUNTERS_PER_LOCATIONS_COUNT, integer(1))
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
                .name(ctx.getArgument(NAME, String.class))
                .sideSize(Distance.ofRegions(ctx.getArgument(SIDE_SIZE, int.class)))
                .environment(parseEnum(ctx, ENVIRONMENT, World.Environment.class))

                .seed(getOrDefault(ctx, SEED, long.class, 0L))
                .spawnLocationsCount(getOrDefault(ctx, SPAWN_LOCATIONS_COUNT, int.class, 16))
                .huntersPerLocationCount(getOrDefault(ctx, HUNTERS_PER_LOCATIONS_COUNT, int.class, 5))
                .build();

        plugin.getTemplateTaskFactory().runCreateTask(sender, config);
        return Command.SINGLE_SUCCESS;
    }
}

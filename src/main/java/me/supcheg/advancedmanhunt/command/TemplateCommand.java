package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.task.TemplateCreateConfig;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.enumArg;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.parseEnum;
import static me.supcheg.advancedmanhunt.command.argument.PathArgument.parsePath;
import static me.supcheg.advancedmanhunt.command.argument.PathArgument.path;

public class TemplateCommand extends AbstractCommand {

    private static final String NAME = "name";
    private static final String SIDE_SIZE = "side_size_in_regions";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";
    private static final String PATH = "path";

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
                        .then(literal("load")
                                .then(path(PATH).executes(this::load)
                                        .then(argument(NAME, string())
                                                .then(argument(SIDE_SIZE, integer(0))
                                                        .executes(this::load)
                                                )
                                        )
                                )

                        )
                        .then(literal("remove")
                                .then(argument(NAME, string())
                                        .suggests(suggestIfStartsWith(ctx -> plugin.getTemplateRepository().getTemplatesMap().keySet()))
                                        .executes(this::remove)
                                )
                        )
                        .then(literal("list").executes(this::list))
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

    @SneakyThrows
    private int load(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        Path path = parsePath(ctx, PATH);
        if (!Files.isDirectory(path)) {
            throw CustomExceptions.NO_DIRECTORY.create(path);
        }

        Path templateInfoPath = path.resolve("template.json");
        Template template;
        if (Files.exists(templateInfoPath)) {
            Template tmp;
            try (BufferedReader reader = Files.newBufferedReader(templateInfoPath)) {
                tmp = plugin.getGson().fromJson(reader, Template.class);
            }
            template = new Template(
                    tmp.getName(),
                    tmp.getSideSize(),
                    path,
                    tmp.getSpawnLocations()
            );
        } else {
            String name;
            int sideSize;
            try {
                name = ctx.getArgument(NAME, String.class);
                sideSize = ctx.getArgument(SIDE_SIZE, int.class);
            } catch (IllegalArgumentException ex) {
                Message.TEMPLATE_LOAD_NO_FILE.send(ctx.getSource().getBukkitSender());
                return Command.SINGLE_SUCCESS;
            }

            template = new Template(name, Distance.ofRegions(sideSize), path, Collections.emptyList());
        }

        plugin.getTemplateRepository().addTemplate(template);
        Message.TEMPLATE_LOAD_SUCCESS.send(ctx.getSource().getBukkitSender());

        return Command.SINGLE_SUCCESS;
    }


    private int remove(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        String name = ctx.getArgument(NAME, String.class);

        Template removed = plugin.getTemplateRepository().removeTemplate(name);

        CommandSender sender = ctx.getSource().getBukkitSender();
        if (removed != null) {
            Message.TEMPLATE_REMOVE_SUCCESS.send(sender, name);
        } else {
            Message.TEMPLATE_REMOVE_NOT_FOUND.send(sender, name);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int list(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        CommandSender sender = ctx.getSource().getBukkitSender();

        Collection<Template> templates = plugin.getTemplateRepository().getTemplates();

        Message.TEMPLATE_LIST_TITLE.send(sender, templates.size());
        if (templates.isEmpty()) {
            Message.TEMPLATE_LIST_EMPTY.send(sender);
        } else {
            for (Template template : templates) {
                Message.TEMPLATE_LIST_SINGLE_INFO.send(sender,
                        template.getName(),
                        template.getSideSize(),
                        template.getFolder(),
                        template.getSpawnLocations().size()
                );
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}

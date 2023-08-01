package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.command.exception.CustomExceptions;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.player.Message;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig.TemplateCreateConfigBuilder;
import me.supcheg.advancedmanhunt.template.TemplateTaskFactory;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.enumArg;
import static me.supcheg.advancedmanhunt.command.argument.EnumArgument.getEnum;
import static me.supcheg.advancedmanhunt.command.argument.PathArgument.getPath;
import static me.supcheg.advancedmanhunt.command.argument.PathArgument.path;

@AllArgsConstructor
public class TemplateCommand extends AbstractCommand {

    private static final String NAME = "name";
    private static final String SIDE_SIZE = "side_size_in_regions";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";
    private static final String PATH = "path";

    private static final String TEMPLATE_EXPORT_FILE = "template.json";

    private final TemplateRepository templateRepository;
    private final TemplateTaskFactory templateTaskFactory;
    private final Gson gson;

    @Override
    public void register(@NotNull CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher) {
        commandDispatcher.register(
                literal("template")
                        .then(literal("generate")
                                .then(argument(NAME, string())
                                        .then(argument(SIDE_SIZE, integer(0))
                                                .then(enumArg(ENVIRONMENT, Environment.class)
                                                        .executes(generate((ctx, cfg) -> cfg))
                                                        .then(argument(SEED, longArg(0))
                                                                .suggests(suggestion(TemplateCreateConfig.DEFAULT_SEED))
                                                                .executes(generate(
                                                                        (ctx, cfg) -> cfg.seed(getLong(ctx, SEED))
                                                                ))
                                                                .then(argument(SPAWN_LOCATIONS_COUNT, integer(0))
                                                                        .suggests(suggestion(TemplateCreateConfig.DEFAULT_SPAWN_LOCATIONS_COUNT))
                                                                        .executes(generate(
                                                                                (ctx, cfg) -> cfg.seed(getLong(ctx, SEED))
                                                                                        .spawnLocationsCount(getInteger(ctx, SPAWN_LOCATIONS_COUNT))
                                                                        ))
                                                                        .then(argument(HUNTERS_PER_LOCATIONS_COUNT, integer(1))
                                                                                .suggests(suggestion(TemplateCreateConfig.DEFAULT_HUNTERS_PER_LOCATIONS))
                                                                                .executes(generate(
                                                                                        (ctx, cfg) -> cfg.seed(getLong(ctx, SEED))
                                                                                                .spawnLocationsCount(getInteger(ctx, SPAWN_LOCATIONS_COUNT))
                                                                                                .huntersPerLocationCount(getInteger(ctx, HUNTERS_PER_LOCATIONS_COUNT))
                                                                                ))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("import")
                                .then(path(PATH).executes(this::importTemplate)
                                        .then(argument(NAME, string())
                                                .then(argument(SIDE_SIZE, integer(0))
                                                        .executes(this::importTemplate)
                                                )
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument(NAME, string())
                                        .suggests(suggestIfStartsWith(ctx -> templateRepository.getTemplatesMap().keySet()))
                                        .executes(this::remove)
                                )
                        )
                        .then(literal("list").executes(this::list))
                        .then(literal("export").then(argument(NAME, string()).executes(this::export)))
        );
    }

    @NotNull
    private Command<BukkitBrigadierCommandSource> generate(@NotNull BiFunction<CommandContext<BukkitBrigadierCommandSource>, TemplateCreateConfigBuilder, TemplateCreateConfigBuilder> additional) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getBukkitSender();

            TemplateCreateConfig config = additional.apply(ctx,
                    TemplateCreateConfig.builder()
                            .name(getString(ctx, NAME))
                            .sideSize(Distance.ofRegions(getInteger(ctx, SIDE_SIZE)))
                            .environment(getEnum(ctx, ENVIRONMENT, Environment.class))
            ).build();

            templateTaskFactory.runCreateTask(sender, config);
            return Command.SINGLE_SUCCESS;
        };
    }

    @SneakyThrows
    private int importTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        Path path = getPath(ctx, PATH);
        if (!Files.isDirectory(path)) {
            throw CustomExceptions.NO_DIRECTORY.create(path);
        }

        Path templateInfoPath = path.resolve(TEMPLATE_EXPORT_FILE);
        Template template;
        if (Files.exists(templateInfoPath)) {
            Template tmp;
            try (BufferedReader reader = Files.newBufferedReader(templateInfoPath)) {
                tmp = gson.fromJson(reader, Template.class);
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
                name = getString(ctx, NAME);
                sideSize = getInteger(ctx, SIDE_SIZE);
            } catch (IllegalArgumentException ex) {
                Message.TEMPLATE_IMPORT_NO_FILE.send(ctx.getSource().getBukkitSender());
                return 0;
            }

            template = new Template(name, Distance.ofRegions(sideSize), path, Collections.emptyList());
        }

        templateRepository.addTemplate(template);
        Message.TEMPLATE_IMPORT_SUCCESS.send(ctx.getSource().getBukkitSender());

        return Command.SINGLE_SUCCESS;
    }


    private int remove(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        String name = getString(ctx, NAME);

        Template removed = templateRepository.removeTemplate(name);

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

        Collection<Template> templates = templateRepository.getTemplates();

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

    @SneakyThrows
    private int export(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        String name = getString(ctx, NAME);

        Template template = templateRepository.getTemplate(name);

        CommandSender sender = ctx.getSource().getBukkitSender();
        if (template == null) {
            Message.TEMPLATE_EXPORT_NOT_FOUND.send(sender, name);
        } else {
            Path exportPath = template.getFolder().resolve(TEMPLATE_EXPORT_FILE);

            try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(Files.newOutputStream(exportPath)))) {
                gson.toJson(template, Template.class, writer);
            }

            Message.TEMPLATE_EXPORT_SUCCESS.send(sender, name, exportPath);
        }

        return Command.SINGLE_SUCCESS;
    }

}

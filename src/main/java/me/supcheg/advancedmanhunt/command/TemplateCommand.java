package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.command.service.TemplateService;
import me.supcheg.advancedmanhunt.command.util.AbstractCommand;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig;
import me.supcheg.advancedmanhunt.template.TemplateCreateConfig.TemplateCreateConfigBuilder;
import me.supcheg.advancedmanhunt.text.MessageText;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collection;
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

@RequiredArgsConstructor
public class TemplateCommand extends AbstractCommand {

    private static final String NAME = "name";
    private static final String RADIUS = "radius_in_regions";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";
    private static final String PATH = "path";

    private final TemplateService service;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("template")
                .then(literal("list").executes(this::listTemplates))
                .then(literal("export").then(argument(NAME, string()).executes(this::exportTemplate)))
                .then(literal("import").then(path(PATH).executes(this::importTemplate)))
                .then(literal("generate")
                        .then(argument(NAME, string())
                                .then(argument(RADIUS, integer(0))
                                        .then(enumArg(ENVIRONMENT, Environment.class)
                                                .executes(generateTemplate((ctx, cfg) -> cfg))
                                                .then(argument(SEED, longArg(0))
                                                        .suggests(suggestion(TemplateCreateConfig.DEFAULT_SEED))
                                                        .executes(generateTemplate(
                                                                (ctx, cfg) -> cfg.seed(getLong(ctx, SEED))
                                                        ))
                                                        .then(argument(SPAWN_LOCATIONS_COUNT, integer(0))
                                                                .suggests(suggestion(TemplateCreateConfig.DEFAULT_SPAWN_LOCATIONS_COUNT))
                                                                .executes(generateTemplate(
                                                                        (ctx, cfg) -> cfg.seed(getLong(ctx, SEED))
                                                                                .spawnLocationsCount(getInteger(ctx, SPAWN_LOCATIONS_COUNT))
                                                                ))
                                                                .then(argument(HUNTERS_PER_LOCATIONS_COUNT, integer(1))
                                                                        .suggests(suggestion(TemplateCreateConfig.DEFAULT_HUNTERS_PER_LOCATIONS))
                                                                        .executes(generateTemplate(
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
                .then(literal("remove")
                        .then(argument(NAME, string())
                                .suggests(suggestIfStartsWith(service::getAllKeys))
                                .executes(this::remove)
                        )
                );
    }

    @NotNull
    private Command<BukkitBrigadierCommandSource> generateTemplate(@NotNull BiFunction<CommandContext<BukkitBrigadierCommandSource>, TemplateCreateConfigBuilder, TemplateCreateConfigBuilder> additional) {
        return ctx -> {
            TemplateCreateConfig config = additional.apply(ctx,
                    TemplateCreateConfig.builder()
                            .name(getString(ctx, NAME))
                            .radius(Distance.ofRegions(getInteger(ctx, RADIUS)))
                            .environment(getEnum(ctx, ENVIRONMENT, Environment.class))
            ).build();

            service.generateTemplate(config);
            return Command.SINGLE_SUCCESS;
        };
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    @SneakyThrows
    private int importTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        Path path = getPath(ctx, PATH);

        service.importTemplate(path);

        MessageText.TEMPLATE_IMPORT_SUCCESS.send(ctx.getSource().getBukkitSender());
        return Command.SINGLE_SUCCESS;
    }


    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int remove(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        String name = getString(ctx, NAME);

        Template template = service.getTemplate(name);
        service.removeTemplate(template);

        MessageText.TEMPLATE_REMOVE_SUCCESS.send(ctx.getSource().getBukkitSender(), name);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int listTemplates(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        CommandSender sender = ctx.getSource().getBukkitSender();

        Collection<Template> templates = service.getAllTemplates();

        MessageText.TEMPLATE_LIST_TITLE.send(sender, templates.size());
        if (templates.isEmpty()) {
            MessageText.TEMPLATE_LIST_EMPTY.send(sender);
        } else {
            for (Template template : templates) {
                MessageText.TEMPLATE_LIST_SINGLE_INFO.send(sender,
                        template.getName(),
                        template.getRadius(),
                        template.getFolder(),
                        template.getSpawnLocations().size()
                );
            }
        }

        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    @SneakyThrows
    private int exportTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        CommandSender sender = ctx.getSource().getBukkitSender();
        String name = getString(ctx, NAME);

        Template template = service.getTemplate(name);
        Path path = service.exportTemplate(template);

        MessageText.TEMPLATE_EXPORT_SUCCESS.send(sender, name, path);
        return Command.SINGLE_SUCCESS;
    }

}

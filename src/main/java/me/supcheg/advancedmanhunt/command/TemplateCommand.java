package me.supcheg.advancedmanhunt.command;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgument;
import me.supcheg.advancedmanhunt.command.argument.TemplateArgument;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateContext;
import me.supcheg.advancedmanhunt.template.TemplateService;
import me.supcheg.advancedmanhunt.text.MessageText;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.argument;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.getSender;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.literal;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.tryGetSenderUniqueId;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TemplateCommand implements BukkitBrigadierCommand {

    private static final String KEY = "key";
    private static final String RADIUS = "radius_in_regions";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";

    private final TemplateService service;

    private final TemplateArgument templateArgument;
    private final KeyArgument keyArgument;
    private final RealEnvironmentArgument environmentArgument;

    @NotNull
    @Override
    public LiteralArgumentBuilder<BukkitBrigadierCommandSource> build() {
        return literal("template")
                .then(literal("list").executes(this::listTemplates))
                .then(literal("generate")
                        .then(keyArgument.key(KEY)
                                .then(argument(RADIUS, integer(0))
                                        .then(environmentArgument.environment(ENVIRONMENT)
                                                .then(argument(SEED, longArg(0))
                                                        .then(argument(SPAWN_LOCATIONS_COUNT, integer(0))
                                                                .then(argument(HUNTERS_PER_LOCATIONS_COUNT, integer(1))
                                                                        .executes(this::generateTemplate)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(literal("remove")
                        .then(templateArgument.template(KEY)
                                .executes(this::remove)
                        )
                );
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    @SneakyThrows
    private int generateTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        TemplateCreateContext config = TemplateCreateContext.builder()
                .receiver(tryGetSenderUniqueId(ctx))
                .name(getString(ctx, KEY))
                .radius(Distance.ofRegions(getInteger(ctx, RADIUS)))
                .environment(environmentArgument.getEnvironment(ctx, ENVIRONMENT))
                .seed(getLong(ctx, SEED))
                .spawnLocationsCount(getInteger(ctx, SPAWN_LOCATIONS_COUNT))
                .huntersPerLocationCount(getInteger(ctx, HUNTERS_PER_LOCATIONS_COUNT))
                .build();

        service.generateTemplate(config);
        return Command.SINGLE_SUCCESS;
    }


    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int remove(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) throws CommandSyntaxException {
        Template template = templateArgument.getTemplate(ctx, KEY);

        service.removeTemplate(template);

        MessageText.TEMPLATE_REMOVE_SUCCESS.send(getSender(ctx), template.getKey());
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int listTemplates(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx) {
        CommandSender sender = getSender(ctx);

        Collection<Template> templates = service.getAllTemplates();

        MessageText.TEMPLATE_LIST_TITLE.send(sender, templates.size());
        if (templates.isEmpty()) {
            MessageText.TEMPLATE_LIST_EMPTY.send(sender);
        } else {
            for (Template template : templates) {
                MessageText.TEMPLATE_LIST_SINGLE_INFO.send(sender,
                        template.getKey(),
                        template.getRadius(),
                        template.getFolder(),
                        template.getSpawnLocations().size()
                );
            }
        }

        return Command.SINGLE_SUCCESS;
    }

}

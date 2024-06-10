package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateCreateContext;
import me.supcheg.advancedmanhunt.template.TemplateService;
import me.supcheg.advancedmanhunt.text.MessageText;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collection;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.argument.ArgumentTypes.key;
import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.tryGetSenderUniqueId;
import static me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgumentType.environment;
import static me.supcheg.advancedmanhunt.command.argument.RealEnvironmentArgumentType.getEnvironment;
import static me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType.getTemplate;
import static me.supcheg.advancedmanhunt.command.argument.TemplateArgumentType.template;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class TemplateCommand implements BrigadierCommand {

    private static final String KEY = "key";
    private static final String RADIUS = "radius_in_regions";
    private static final String ENVIRONMENT = "environment";
    private static final String SEED = "seed";
    private static final String SPAWN_LOCATIONS_COUNT = "spawn_locations";
    private static final String HUNTERS_PER_LOCATIONS_COUNT = "hunters_per_locations";

    private final TemplateService service;

    @NotNull
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build() {
        return literal("template")
                .then(literal("list").executes(this::listTemplates))
                .then(literal("generate")
                        .then(argument(KEY, key())
                                .then(argument(RADIUS, integer(0))
                                        .then(argument(ENVIRONMENT, environment())
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
                        .then(argument(KEY, template(service))
                                .executes(this::remove)
                        )
                );
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    @SneakyThrows
    private int generateTemplate(@NotNull CommandContext<CommandSourceStack> ctx) {
        TemplateCreateContext config = TemplateCreateContext.builder()
                .receiver(tryGetSenderUniqueId(ctx))
                .name(ctx.getArgument(KEY, Key.class).asString())
                .radius(Distance.ofRegions(getInteger(ctx, RADIUS)))
                .environment(getEnvironment(ctx, ENVIRONMENT))
                .seed(getLong(ctx, SEED))
                .spawnLocationsCount(getInteger(ctx, SPAWN_LOCATIONS_COUNT))
                .huntersPerLocationCount(getInteger(ctx, HUNTERS_PER_LOCATIONS_COUNT))
                .build();

        service.generateTemplate(config);
        return Command.SINGLE_SUCCESS;
    }


    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int remove(@NotNull CommandContext<CommandSourceStack> ctx) {
        Template template = getTemplate(ctx, KEY);

        service.removeTemplate(template);

        MessageText.TEMPLATE_REMOVE_SUCCESS.send(ctx.getSource().getSender(), template.getKey());
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("SameReturnValue") // command entrypoint
    private int listTemplates(@NotNull CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

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

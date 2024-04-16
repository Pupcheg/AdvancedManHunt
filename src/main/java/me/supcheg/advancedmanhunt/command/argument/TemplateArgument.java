package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.brigadier.PaperBrigadier;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands.suggestIfStartsWith;

public class TemplateArgument {
    private static final SimpleCommandExceptionType NO_TEMPLATE = new SimpleCommandExceptionType(
            PaperBrigadier.message(Component.translatable("advancedmanhunt.exception.no_template"))
    );

    private final KeyArgument keyArgument;
    private final TemplateService service;
    private final SuggestionProvider<BukkitBrigadierCommandSource> templateKeysProvider;

    @Inject
    public TemplateArgument(@NotNull KeyArgument keyArgument, @NotNull TemplateService service) {
        this.keyArgument = keyArgument;
        this.service = service;
        this.templateKeysProvider = suggestIfStartsWith(service.getStringKeys());
    }

    @NotNull
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> template(@NotNull String name) {
        return keyArgument.key(name).suggests(templateKeysProvider);
    }

    @NotNull
    public Template getTemplate(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        Template template = service.getTemplate(keyArgument.getKey(ctx, name));
        if (template == null) {
            throw NO_TEMPLATE.create();
        }
        return template;
    }
}

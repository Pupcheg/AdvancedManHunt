package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class TemplateArgumentType implements CustomArgumentType<Template, Key> {
    private static final SimpleCommandExceptionType NO_TEMPLATE = new SimpleCommandExceptionType(
            MessageComponentSerializer.message().serialize(Component.translatable("advancedmanhunt.exception.no_template"))
    );

    private final TemplateService service;
    private final ArgumentType<Key> keyArgumentType;

    private TemplateArgumentType(@NotNull TemplateService service) {
        this.service = service;
        this.keyArgumentType = ArgumentTypes.key();
    }

    @NotNull
    public static ArgumentType<Template> template(@NotNull TemplateService service) {
        return new TemplateArgumentType(service);
    }

    @NotNull
    public static Template getTemplate(@NotNull CommandContext<CommandSourceStack> ctx, @NotNull String name) {
        return ctx.getArgument(name, Template.class);
    }

    @NotNull
    @Override
    public Template parse(@NotNull StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        Key key = keyArgumentType.parse(reader);

        Template template = service.getTemplate(key);

        if (template == null) {
            reader.setCursor(cursor);
            throw NO_TEMPLATE.create();
        }
        return template;
    }

    @NotNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        service.getStringKeys().forEach(builder::suggest);
        return builder.buildFuture();
    }

    @NotNull
    @Override
    public ArgumentType<Key> getNativeType() {
        return keyArgumentType;
    }
}

package me.supcheg.advancedmanhunt.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class EnumArgumentType<T extends Enum<T>> implements CustomArgumentType<T, String> {

    private final Class<T> enumClazz;
    private final List<String> values;
    private final ArgumentType<String> stringArgumentType;

    protected EnumArgumentType(@NotNull Class<T> enumClazz) {
        this.enumClazz = enumClazz;
        this.values = Arrays.stream(enumClazz.getEnumConstants())
                .map(Enum::name)
                .map(String::toLowerCase)
                .toList();
        this.stringArgumentType = StringArgumentType.string();
    }

    @NotNull
    @Override
    public T parse(@NotNull StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String raw = reader.readUnquotedString();
        try {
            return Enum.valueOf(enumClazz, raw);
        } catch (IllegalArgumentException e) {
            reader.setCursor(cursor);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        }
    }

    @NotNull
    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                              @NotNull SuggestionsBuilder builder) {
        values.forEach(builder::suggest);
        return builder.buildFuture();
    }

    @NotNull
    @Override
    public ArgumentType<String> getNativeType() {
        return stringArgumentType;
    }
}

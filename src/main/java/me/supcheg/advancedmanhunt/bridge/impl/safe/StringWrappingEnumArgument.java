package me.supcheg.advancedmanhunt.bridge.impl.safe;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import me.supcheg.advancedmanhunt.command.BukkitBrigadierCommands;
import org.jetbrains.annotations.NotNull;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class StringWrappingEnumArgument implements EnumArgument {
    @NotNull
    @Override
    public <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> enumArg(
            @NotNull String name, @NotNull Class<E> enumType) {
        return RequiredArgumentBuilder.argument(name, word());
    }

    @NotNull
    @Override
    public <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> enumArgWithSuggestions(
            @NotNull String name, @NotNull Class<E> enumType) {
        return enumArg(name, enumType).suggests(BukkitBrigadierCommands.suggestEnumConstants(enumType));
    }

    @NotNull
    @Override
    public <E extends Enum<E>> E getEnum(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx,
                                         @NotNull String name, @NotNull Class<E> enumType) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(ctx, name).toUpperCase();
        try {
            return Enum.valueOf(enumType, raw);
        } catch (Exception e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                    .createWithContext(new StringReader(ctx.getInput()));
        }
    }
}

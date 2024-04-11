package me.supcheg.advancedmanhunt.bridge.impl.safe;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.supcheg.advancedmanhunt.bridge.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class StringWrappingEnumArgument implements EnumArgument {
    @NotNull
    @Override
    public <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> enumArg(
            @NotNull String name, @NotNull Class<E> enumType) {

        E[] enumConstants = enumType.getEnumConstants();
        List<String> serializedConstants = new ArrayList<>(enumConstants.length);
        for (E enumConstant : enumConstants) {
            serializedConstants.add(enumConstant.toString().toLowerCase());
        }

        return RequiredArgumentBuilder.<BukkitBrigadierCommandSource, String>argument(name, word())
                .suggests((context, builder) -> {
                    for (String serializedConstant : serializedConstants) {
                        if (serializedConstant.startsWith(builder.getRemainingLowerCase())) {
                            builder.suggest(serializedConstant);
                        }
                    }
                    return builder.buildFuture();
                });
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

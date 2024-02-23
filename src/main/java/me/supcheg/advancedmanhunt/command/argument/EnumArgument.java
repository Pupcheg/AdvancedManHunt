package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumArgument {
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <E extends Enum<E>> RequiredArgumentBuilder<BukkitBrigadierCommandSource, String> enumArg(
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
    public static <E extends Enum<E>> E getEnum(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx,
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

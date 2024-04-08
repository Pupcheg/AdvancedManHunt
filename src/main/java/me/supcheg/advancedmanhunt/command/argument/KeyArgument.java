package me.supcheg.advancedmanhunt.command.argument;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.injector.WrappingKeyArgument;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeyArgument {
    @NotNull
    @Contract(pure = true)
    public static RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name) {
        return WrappingKeyArgument.key(name);
    }

    @NotNull
    public static Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        return WrappingKeyArgument.getKey(ctx, name);
    }

}

package me.supcheg.advancedmanhunt.injector;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.key.Key;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WrappingKeyArgument {
    @NotNull
    @Contract(pure = true)
    public static RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, ResourceLocationArgument.id());
    }

    @SuppressWarnings({"PatternValidation", "unchecked"})
    @NotNull
    public static Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name)
            throws CommandSyntaxException {
        CommandContext<CommandSourceStack> casted = (CommandContext<CommandSourceStack>) (Object) ctx;
        ResourceLocation location = ResourceLocationArgument.getId(casted, name);
        return Key.key(location.getNamespace(), location.getPath());
    }
}

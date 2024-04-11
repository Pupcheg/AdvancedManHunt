package me.supcheg.advancedmanhunt.bridge.impl.nms;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.bridge.command.KeyArgument;
import net.kyori.adventure.key.Key;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class NmsKeyArgument implements KeyArgument {
    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> key(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, ResourceLocationArgument.id());
    }

    @SuppressWarnings("PatternValidation")
    @NotNull
    @Override
    public Key getKey(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        ResourceLocation location = ResourceLocationArgument.getId(uncheckedCast(ctx), name);
        return Key.key(location.getNamespace(), location.getPath());
    }
}

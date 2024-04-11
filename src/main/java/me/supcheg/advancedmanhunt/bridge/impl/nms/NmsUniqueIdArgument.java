package me.supcheg.advancedmanhunt.bridge.impl.nms;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.supcheg.advancedmanhunt.bridge.command.UniqueIdArgument;
import net.minecraft.commands.arguments.UuidArgument;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class NmsUniqueIdArgument implements UniqueIdArgument {
    @NotNull
    @Override
    public RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> uniqueId(@NotNull String name) {
        return RequiredArgumentBuilder.argument(name, UuidArgument.uuid());
    }

    @NotNull
    @Override
    public UUID getUniqueId(@NotNull CommandContext<BukkitBrigadierCommandSource> ctx, @NotNull String name) {
        return UuidArgument.getUuid(uncheckedCast(ctx), name);
    }
}

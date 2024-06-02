package me.supcheg.advancedmanhunt.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.config.IntLimit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitBrigadierCommands {
    @NotNull
    public static Player getPlayer(@NotNull CommandContext<CommandSourceStack> ctx) {
        return Objects.requireNonNull((Player) ctx.getSource().getExecutor(), "player");
    }

    @Nullable
    public static UUID tryGetSenderUniqueId(@NotNull CommandContext<CommandSourceStack> ctx) {
        Entity bukkitEntity = ctx.getSource().getExecutor();
        return bukkitEntity == null ? null : bukkitEntity.getUniqueId();
    }

    @NotNull
    public static IntegerArgumentType asIntArgument(@NotNull IntLimit limit) {
        return IntegerArgumentType.integer(limit.getMinValue(), limit.getMaxValue());
    }
}

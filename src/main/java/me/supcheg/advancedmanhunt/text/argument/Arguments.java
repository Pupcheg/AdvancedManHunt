package me.supcheg.advancedmanhunt.text.argument;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.player.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Arguments {
    public static void send(@NotNull CommandSender player, @NotNull Supplier<Component> supplier) {
        player.sendMessage(supplier.get());
    }

    public static void send(@NotNull UUID uniqueId, @NotNull Supplier<Component> supplier) {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null) {
            player.sendMessage(supplier.get());
        }
    }

    public static void sendPlayers(@NotNull Iterable<? extends CommandSender> players, @NotNull Supplier<Component> supplier) {
        Component built = null;
        for (CommandSender player : players) {
            if (built == null) {
                built = supplier.get();
            }
            player.sendMessage(built);
        }
    }

    public static void sendUniqueIds(@NotNull Iterable<UUID> uniqueIds, @NotNull Supplier<Component> supplier) {
        Component built = null;
        for (UUID uniqueId : uniqueIds) {
            Player player = Bukkit.getPlayer(uniqueId);

            if (player != null) {
                if (built == null) {
                    built = supplier.get();
                }
                player.sendMessage(built);
            }
        }
    }

    public static void sendNullableAndConsole(@Nullable UUID uniqueId, @NotNull Supplier<Component> supplier) {
        Component built = supplier.get();
        Bukkit.getConsoleSender().sendMessage(built);

        if (uniqueId != null) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                player.sendMessage(built);
            }
        }
    }

    public static void broadcast(@NotNull Supplier<Component> supplier) {
        Bukkit.broadcast(supplier.get(), Permission.NOTIFICATIONS);
    }
}

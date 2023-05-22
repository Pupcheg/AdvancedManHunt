package me.supcheg.advancedmanhunt.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Notifications {

    public static void sendError(@NotNull String format, @Nullable Object @NotNull ... objects) {
        send(Component.text(format.formatted(objects), NamedTextColor.RED));
    }

    public static void sendSuccess(@NotNull String format, @Nullable Object @NotNull ... objects) {
        send(Component.text(format.formatted(objects), NamedTextColor.GREEN));
    }

    public static void send(@NotNull Component notification) {
        Bukkit.getConsoleSender().sendMessage(notification);
        Bukkit.broadcast(notification, Permissions.NOTIFICATIONS);
    }
}

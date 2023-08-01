package me.supcheg.advancedmanhunt.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerUtil {

    @Contract(pure = true)
    public static boolean isOnline(@NotNull UUID uniqueId) {
        return Bukkit.getPlayer(uniqueId) != null;
    }

    @Contract(pure = true)
    public static boolean isAllOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return true;
        }

        for (UUID uniqueId : uniqueIds) {
            if (!isOnline(uniqueId)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static boolean isAnyOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return false;
        }

        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                return true;
            }
        }
        return false;
    }

    @Contract(pure = true)
    public static boolean isNoneOnline(@NotNull Collection<UUID> uniqueIds) {
        if (uniqueIds.isEmpty()) {
            return true;
        }

        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                return false;
            }
        }
        return true;
    }

    @Contract(pure = true)
    public static int countOnlinePlayers(@NotNull Iterable<UUID> uniqueIds) {
        int count = 0;
        for (UUID uniqueId : uniqueIds) {
            if (isOnline(uniqueId)) {
                count++;
            }
        }
        return count;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static List<Player> asPlayersList(@NotNull Collection<UUID> uniqueIds) {
        List<Player> collection = new ArrayList<>(uniqueIds.size());
        for (UUID uniqueId : uniqueIds) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                collection.add(player);
            }
        }
        return collection;
    }

    @Contract(pure = true)
    public static void forEach(@NotNull Collection<UUID> uniqueIds, @NotNull Consumer<Player> consumer) {
        for (UUID uniqueId : uniqueIds) {
            Player player = Bukkit.getPlayer(uniqueId);
            if (player != null) {
                consumer.accept(player);
            }
        }
    }

}

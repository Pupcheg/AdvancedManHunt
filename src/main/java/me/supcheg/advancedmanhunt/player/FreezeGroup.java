package me.supcheg.advancedmanhunt.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface FreezeGroup {
    default void add(@NotNull Player player) {
        add(player.getUniqueId());
    }

    void add(@NotNull UUID uniqueId);

    default void remove(@NotNull Player player) {
        remove(player.getUniqueId());
    }

    void remove(@NotNull UUID uniqueId);

    void clear();
}

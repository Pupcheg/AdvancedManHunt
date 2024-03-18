package me.supcheg.advancedmanhunt.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerFreezer {
    default void freeze(@NotNull Player player) {
        freeze(player.getUniqueId());
    }

    void freeze(@NotNull UUID uniqueId);

    default void unfreeze(@NotNull Player player) {
        unfreeze(player.getUniqueId());
    }

    void unfreeze(@NotNull UUID uniqueId);

    default boolean isFrozen(@NotNull Player player) {
        return isFrozen(player.getUniqueId());
    }

    boolean isFrozen(@NotNull UUID uniqueId);

    @NotNull FreezeGroup newFreezeGroup();
}

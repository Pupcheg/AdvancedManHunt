package me.supcheg.advancedmanhunt.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerFreezer {
    void freeze(@NotNull Player player);

    void unfreeze(@NotNull Player player);

    boolean isFrozen(@NotNull Player player);

    @NotNull FreezeGroup newFreezeGroup();
}

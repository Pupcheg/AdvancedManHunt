package me.supcheg.advancedmanhunt.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface FreezeGroup {
    void add(@NotNull Player player);

    void remove(@NotNull Player player);

    void clear();
}

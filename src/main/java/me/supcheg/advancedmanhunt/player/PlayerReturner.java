package me.supcheg.advancedmanhunt.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerReturner {
    void returnPlayer(@NotNull Player player);

    default void returnPlayers(@NotNull Iterable<Player> players) {
        for (Player player : players) {
            returnPlayer(player);
        }
    }
}

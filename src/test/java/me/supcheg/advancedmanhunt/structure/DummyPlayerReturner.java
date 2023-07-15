package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.player.PlayerReturner;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DummyPlayerReturner implements PlayerReturner {

    @Override
    public void returnPlayers(@NotNull Iterable<Player> players) {
    }

    @Override
    public void returnPlayer(@NotNull Player player) {
    }
}

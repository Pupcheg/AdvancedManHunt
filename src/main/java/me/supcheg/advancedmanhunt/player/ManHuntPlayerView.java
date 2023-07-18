package me.supcheg.advancedmanhunt.player;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ManHuntPlayerView {

    @NotNull
    UUID getUniqueId();

    @Nullable
    ManHuntGame getGame();

    void setGame(@Nullable ManHuntGame game);

    @NotNull
    default OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getUniqueId());
    }

    @Nullable
    default Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    default boolean isOnline() {
        return getPlayer() != null;
    }
}

package me.supcheg.advancedmanhunt.player;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.paper.ComponentFormatter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ManHuntPlayerView extends Audience {

    @NotNull
    UUID getUniqueId();

    @Nullable
    ManHuntGame getGame();

    void setGame(@Nullable ManHuntGame game);

    @Nullable
    default ManHuntRole getRole() {
        return getGame() == null ? null : getGame().getRole(this);
    }

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

    default void sendMessage(@NotNull Component text, @Nullable Object format) {
        sendMessage(ComponentFormatter.format(text, format));
    }

    default void sendMessage(@NotNull Component text, @Nullable Object @NotNull ... format) {
        sendMessage(ComponentFormatter.format(text, format));
    }

    default void sendMessage(@NotNull Component text) {
        Player player = getPlayer();
        if (player != null) {
            player.sendMessage(text);
        }
    }

    default void playSound(@NotNull Sound sound) {
        Player player = getPlayer();
        if (player != null) {
            player.playSound(sound);
        }
    }
}

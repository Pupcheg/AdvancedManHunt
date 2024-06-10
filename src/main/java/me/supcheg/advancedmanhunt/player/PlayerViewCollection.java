package me.supcheg.advancedmanhunt.player;

import me.supcheg.advancedmanhunt.player.impl.PlayerViewCollectionImpl;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public interface PlayerViewCollection {

    @NotNull
    @Contract(value = "-> new", pure = true)
    static PlayerViewCollection hashSet() {
        return wrap(new HashSet<>());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    static PlayerViewCollection wrap(@NotNull Collection<UUID> collection) {
        Objects.requireNonNull(collection, "collection");
        return new PlayerViewCollectionImpl(collection);
    }

    @NotNull
    Collection<UUID> uniqueIds();

    @NotNull
    Collection<Player> onlinePlayers();

    @NotNull
    Collection<OfflinePlayer> offlinePlayers();
}

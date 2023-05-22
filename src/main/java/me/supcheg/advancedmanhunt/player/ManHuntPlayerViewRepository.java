package me.supcheg.advancedmanhunt.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface ManHuntPlayerViewRepository {
    @NotNull
    @UnmodifiableView
    Collection<ManHuntPlayerView> getPlayers();

    @NotNull
    default ManHuntPlayerView get(@NotNull Player player) {
        return get(player.getUniqueId());
    }

    @Nullable
    default ManHuntPlayerView get(@NotNull String name) {
        Player exact = Bukkit.getPlayerExact(name);
        return exact != null ? get(exact.getUniqueId()) : null;
    }

    @NotNull
    ManHuntPlayerView get(@NotNull UUID uniqueId);
}

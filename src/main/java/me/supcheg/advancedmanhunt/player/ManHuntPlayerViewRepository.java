package me.supcheg.advancedmanhunt.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ManHuntPlayerViewRepository {
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

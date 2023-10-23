package me.supcheg.advancedmanhunt.game;

import me.supcheg.advancedmanhunt.storage.EntityRepository;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ManHuntGameRepository extends EntityRepository<ManHuntGame, UUID> {
    @NotNull
    ManHuntGame create(@NotNull UUID owner, int maxHunters, int maxSpectators);

    @Nullable
    ManHuntGame find(@NotNull Location location);
}

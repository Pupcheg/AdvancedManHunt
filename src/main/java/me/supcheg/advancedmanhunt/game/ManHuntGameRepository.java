package me.supcheg.advancedmanhunt.game;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface ManHuntGameRepository {
    @NotNull
    ManHuntGame create(@NotNull UUID owner, int maxHunters, int maxSpectators);

    void invalidate(@NotNull ManHuntGame game);

    @Nullable
    ManHuntGame find(@NotNull UUID uniqueId);

    @Nullable
    ManHuntGame find(@NotNull Location location);

    @NotNull
    @UnmodifiableView
    Collection<ManHuntGame> getManHuntGames();
}

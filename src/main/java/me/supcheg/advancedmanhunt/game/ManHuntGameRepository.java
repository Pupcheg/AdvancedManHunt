package me.supcheg.advancedmanhunt.game;

import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public interface ManHuntGameRepository {
    @NotNull
    ManHuntGame create(@NotNull ManHuntPlayerView owner, int maxHunters, int maxSpectators);

    @Nullable
    ManHuntGame find(@NotNull UUID uniqueId);

    @NotNull
    @UnmodifiableView
    Collection<ManHuntGame> getManHuntGames();
}

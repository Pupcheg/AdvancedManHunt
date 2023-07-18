package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.region.GameRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ManHuntGame {
    @NotNull
    UUID getUniqueId();

    @NotNull
    ManHuntPlayerView getOwner();

    @NotNull
    GameState getState();

    default boolean isPlaying() {
        return getState() == GameState.START || getState() == GameState.PLAY;
    }

    int getMaxHunters();

    int getMaxSpectators();

    void start(@NotNull ManHuntGameConfiguration configuration);

    void stop(@Nullable ManHuntRole winnerRole);

    @Nullable
    @CanIgnoreReturnValue
    ManHuntRole addPlayer(@NotNull ManHuntPlayerView playerView);

    @Nullable
    ManHuntRole getRole(@NotNull ManHuntPlayerView playerView);

    @CanIgnoreReturnValue
    boolean addPlayer(@NotNull ManHuntPlayerView playerView, @NotNull ManHuntRole role);

    boolean canAcceptPlayer();

    boolean canAcceptSpectator();

    boolean canStart();

    @NotNull
    @UnmodifiableView
    Collection<ManHuntPlayerView> getMembers();

    @NotNull
    @UnmodifiableView
    Collection<ManHuntPlayerView> getPlayers();

    @Nullable
    ManHuntPlayerView getRunner();

    @NotNull
    @UnmodifiableView
    Set<ManHuntPlayerView> getHunters();

    @NotNull
    @UnmodifiableView
    Set<ManHuntPlayerView> getSpectators();

    @NotNull
    GameRegion getRegion(@NotNull World.Environment environment);

    @NotNull
    GameRegion getOverWorldRegion();

    @NotNull
    GameRegion getNetherRegion();

    @NotNull
    GameRegion getEndRegion();

    @Nullable
    Location getSpawnLocation();
}

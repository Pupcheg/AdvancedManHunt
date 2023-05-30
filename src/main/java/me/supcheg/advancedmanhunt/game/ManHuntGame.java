package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.region.GameRegion;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
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
        return getState() == GameState.START && getState() == GameState.PLAY;
    }

    int getMaxHunters();

    int getMaxSpectators();

    void start(@NotNull ManHuntGameConfiguration configuration);

    /**
     * @param winnerRole {@link ManHuntRole#RUNNER}, {@link ManHuntRole#HUNTER} or {@code null}
     */
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
    @Unmodifiable
    Collection<ManHuntPlayerView> getMembers();

    @NotNull
    List<ManHuntPlayerView> getPlayers();

    @Nullable
    ManHuntPlayerView getRunner();

    @NotNull
    @Unmodifiable
    Set<ManHuntPlayerView> getHunters();

    @NotNull
    @Unmodifiable
    Set<ManHuntPlayerView> getSpectators();

    @NotNull
    GameRegion getRegion(@NotNull World.Environment environment);

    @NotNull
    GameRegion getOverWorldRegion();

    @NotNull
    GameRegion getNetherRegion();

    @NotNull
    GameRegion getEndRegion();
}

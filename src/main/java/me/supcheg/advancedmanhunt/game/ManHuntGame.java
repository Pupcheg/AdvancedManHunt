package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.gui.ConfigurateGameGui;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ManHuntGame {
    @NotNull
    UUID getUniqueId();

    @NotNull
    UUID getOwner();

    @NotNull
    GameState getState();

    default boolean isPlaying() {
        return getState() == GameState.START || getState() == GameState.PLAY;
    }

    @NotNull
    ManHuntGameConfiguration getConfig();

    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<Boolean> start();

    void stop(@Nullable ManHuntRole winnerRole);

    @Nullable
    @CanIgnoreReturnValue
    ManHuntRole addMember(@NotNull UUID uniqueId);

    @Nullable
    ManHuntRole getRole(@NotNull UUID uniqueId);

    @CanIgnoreReturnValue
    boolean addMember(@NotNull UUID uniqueId, @NotNull ManHuntRole role);

    boolean canAcceptPlayer();

    boolean canAcceptSpectator();

    boolean canStart();

    @NotNull
    @UnmodifiableView
    Collection<UUID> getMembers();

    @NotNull
    @UnmodifiableView
    Collection<UUID> getPlayers();

    @UnknownNullability
    UUID getRunner();

    @NotNull
    @UnmodifiableView
    Set<UUID> getHunters();

    @NotNull
    @UnmodifiableView
    Set<UUID> getSpectators();

    @UnknownNullability
    default GameRegion getRegion(@NotNull RealEnvironment environment) {
        return switch (environment) {
            case OVERWORLD -> getOverworld();
            case NETHER -> getNether();
            case THE_END -> getEnd();
        };
    }

    @UnknownNullability
    GameRegion getOverworld();

    @UnknownNullability
    GameRegion getNether();

    @UnknownNullability
    GameRegion getEnd();

    @Nullable
    ImmutableLocation getSpawnLocation();

    @NotNull
    ConfigurateGameGui getConfigGui();
}

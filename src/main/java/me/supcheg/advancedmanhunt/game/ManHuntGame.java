package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.gui.ConfigurateGameGui;
import me.supcheg.advancedmanhunt.region.GameRegion;
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
    UUID getOwner();

    @NotNull
    GameState getState();

    default boolean isPlaying() {
        return getState() == GameState.START || getState() == GameState.PLAY;
    }

    @NotNull
    ManHuntGameConfiguration getConfig();

    void start();

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

    @Nullable
    UUID getRunner();

    @NotNull
    @UnmodifiableView
    Set<UUID> getHunters();

    @NotNull
    @UnmodifiableView
    Set<UUID> getSpectators();

    @NotNull
    default GameRegion getRegion(@NotNull World.Environment environment) {
        return switch (environment) {
            case NORMAL -> getOverWorldRegion();
            case NETHER -> getNetherRegion();
            case THE_END -> getEndRegion();
            default -> throw new IllegalArgumentException(environment.toString());
        };
    }

    @NotNull
    GameRegion getOverWorldRegion();

    @NotNull
    GameRegion getNetherRegion();

    @NotNull
    GameRegion getEndRegion();

    @Nullable
    ImmutableLocation getSpawnLocation();

    @NotNull
    ConfigurateGameGui getConfigurateGui();
}

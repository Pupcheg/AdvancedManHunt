package me.supcheg.advancedmanhunt.game;

import me.supcheg.advancedmanhunt.player.PlayerViewCollection;
import me.supcheg.advancedmanhunt.util.OtherCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.UUID;


public final class ManHuntGameMembers {
    private final PlayerViewCollection runnerAsCollection;
    private final PlayerViewCollection hunters;
    private final PlayerViewCollection spectators;

    private final PlayerViewCollection players;
    private final PlayerViewCollection all;

    ManHuntGameMembers() {
        this.runnerAsCollection = PlayerViewCollection.hashSet();
        this.hunters = PlayerViewCollection.hashSet();
        this.spectators = PlayerViewCollection.hashSet();

        this.players = PlayerViewCollection.wrap(
                OtherCollections.join(runnerAsCollection.uniqueIds(), hunters.uniqueIds())
        );
        this.all = PlayerViewCollection.wrap(
                OtherCollections.join(players.uniqueIds(), spectators.uniqueIds())
        );
    }

    @UnknownNullability
    public UUID runner() {
        Iterator<UUID> it = runners().uniqueIds().iterator();
        return it.hasNext() ? it.next() : null;
    }

    @NotNull
    public PlayerViewCollection forRole(@NotNull ManHuntRole role) {
        return switch (role) {
            case RUNNER -> runnerAsCollection;
            case HUNTER -> hunters;
            case SPECTATOR -> spectators;
        };
    }

    /**
     * Includes {@link ManHuntRole#RUNNER}
     */
    @NotNull
    public PlayerViewCollection runners() {
        return runnerAsCollection;
    }

    /**
     * {@link ManHuntRole#HUNTER}
     */
    @NotNull
    public PlayerViewCollection hunters() {
        return hunters;
    }

    /**
     * {@link ManHuntRole#SPECTATOR}
     */
    @NotNull
    public PlayerViewCollection spectators() {
        return spectators;
    }

    /**
     * {@link ManHuntRole#RUNNER} and {@link ManHuntRole#HUNTER}
     */
    @NotNull
    @UnmodifiableView
    public PlayerViewCollection players() {
        return players;
    }

    /**
     * {@link ManHuntRole#RUNNER}, {@link ManHuntRole#HUNTER} and {@link ManHuntRole#SPECTATOR}
     */
    @NotNull
    @UnmodifiableView
    public PlayerViewCollection all() {
        return all;
    }
}

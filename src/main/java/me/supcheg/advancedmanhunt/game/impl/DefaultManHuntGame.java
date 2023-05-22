package me.supcheg.advancedmanhunt.game.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.PlayerViews;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

class DefaultManHuntGame implements ManHuntGame {

    private final DefaultManHuntGameService gameService;

    private final UUID uniqueId;
    private final ManHuntPlayerView owner;

    private final int maxHunters;
    private final int maxSpectators;

    private final SetMultimap<ManHuntRole, ManHuntPlayerView> allMembers;
    private final Set<ManHuntPlayerView> unmodifiableHunters;
    private final Set<ManHuntPlayerView> unmodifiableSpectators;
    private final Collection<ManHuntPlayerView> unmodifiableMembers;

    private GameState state;

    // used after initialize
    private GameRegion overWorld;
    private GameRegion nether;
    private GameRegion end;
    private Location spawnLocation;
    private Set<CountDownTimer> timers;
    private Map<Environment, Location> environment2runnerLastLocation;

    DefaultManHuntGame(@NotNull DefaultManHuntGameService gameService,
                       @NotNull UUID uniqueId,
                       @NotNull ManHuntPlayerView owner,
                       int maxHunters, int maxSpectators) {
        this.gameService = gameService;

        this.owner = owner;
        this.uniqueId = uniqueId;
        this.state = GameState.CREATE;

        this.maxHunters = maxHunters;
        this.maxSpectators = maxSpectators;

        this.allMembers = MultimapBuilder
                .enumKeys(ManHuntRole.class)
                .hashSetValues(Math.max(maxHunters, maxSpectators))
                .build();
        this.unmodifiableHunters = Collections.unmodifiableSet(allMembers.get(ManHuntRole.HUNTER));
        this.unmodifiableSpectators = Collections.unmodifiableSet(allMembers.get(ManHuntRole.SPECTATOR));
        this.unmodifiableMembers = Collections.unmodifiableCollection(allMembers.values());
    }

    void setState(@NotNull GameState state) {
        if (state.lower(this.state)) {
            throw new IllegalStateException("Switching to lower state! " + this.state + " -> " + state + ", game: " + this);
        }
        this.state = state;
    }

    @NotNull
    Map<Environment, Location> getEnvironmentToRunnerLastLocation() {
        if (environment2runnerLastLocation == null) {
            environment2runnerLastLocation = new EnumMap<>(Environment.class);
        }
        return environment2runnerLastLocation;
    }

    @NotNull
    Collection<CountDownTimer> getTimers() {
        if (timers == null) {
            timers = new HashSet<>();
        }
        return timers;
    }

    void setOverWorldRegion(@NotNull GameRegion overWorld) {
        this.overWorld = overWorld;
    }

    void setNetherRegion(@NotNull GameRegion nether) {
        this.nether = nether;
    }

    void setEndRegion(@NotNull GameRegion end) {
        this.end = end;
    }

    Location getSpawnLocation() {
        return spawnLocation;
    }

    void setSpawnLocation(@NotNull Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @NotNull
    SetMultimap<ManHuntRole, ManHuntPlayerView> getAllMembers() {
        return allMembers;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    @NotNull
    public ManHuntPlayerView getOwner() {
        return owner;
    }

    @Override
    @NotNull
    public GameState getState() {
        return state;
    }

    @Override
    public int getMaxHunters() {
        return maxHunters;
    }

    @Override
    public int getMaxSpectators() {
        return maxSpectators;
    }

    @Override
    public void start(@NotNull ManHuntGameConfiguration configuration) {
        gameService.start(this, configuration);
    }

    @Override
    public void stop(@NotNull String reason) {
        gameService.stop(this);
    }

    @Override
    @Nullable
    @CanIgnoreReturnValue
    public ManHuntRole addPlayer(@NotNull ManHuntPlayerView playerView) {
        ManHuntRole returnRole = null;

        if (state != GameState.CREATE) {
            if (!allMembers.containsValue(playerView)) {
                if (ManHuntRole.SPECTATOR.canJoin(this)) {
                    allMembers.put(ManHuntRole.SPECTATOR, playerView);
                    returnRole = ManHuntRole.SPECTATOR;
                }
            }
        } else {
            if (!allMembers.containsValue(playerView)) {
                for (ManHuntRole role : ManHuntRole.VALUES) {
                    if (role.canJoin(this)) {
                        allMembers.put(role, playerView);
                        returnRole = role;
                        break;
                    }
                }
            }
        }
        playerView.setGame(this);
        return returnRole;
    }

    @Override
    @Nullable
    public ManHuntRole getRole(@NotNull ManHuntPlayerView playerView) {
        ManHuntRole playerRole = null;
        for (ManHuntRole role : ManHuntRole.VALUES) {
            if (role.getPlayers(this).contains(playerView)) {
                playerRole = role;
                break;
            }
        }
        return playerRole;
    }

    @Override
    @CanIgnoreReturnValue
    public boolean addPlayer(@NotNull ManHuntPlayerView playerView, @NotNull ManHuntRole role) {
        if (role != ManHuntRole.SPECTATOR && state != GameState.CREATE) {
            throw new IllegalStateException("Unable to add players to a game already started");
        }

        if (role.canJoin(this)) {
            allMembers.put(role, playerView);
            playerView.setGame(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean canAcceptPlayer() {
        return state == GameState.CREATE &&
                (ManHuntRole.RUNNER.canJoin(this) || ManHuntRole.HUNTER.canJoin(this));
    }

    @Override
    public boolean canAcceptSpectator() {
        return ManHuntRole.SPECTATOR.canJoin(this);
    }

    @Override
    public boolean canStart() {
        return state == GameState.CREATE
                && PlayerViews.nonNullAndOnline(getRunner())
                && PlayerViews.isAnyOnline(getHunters());
    }

    @Override
    @NotNull
    @Unmodifiable
    public Collection<ManHuntPlayerView> getMembers() {
        return unmodifiableMembers;
    }

    @Override
    @NotNull
    public List<ManHuntPlayerView> getPlayers() {
        List<ManHuntPlayerView> players;

        ManHuntPlayerView runner = getRunner();
        if (runner == null) {
            players = new ArrayList<>(getHunters());
        } else {
            players = new ArrayList<>(1 + getHunters().size());
            players.add(runner);
            players.addAll(getHunters());
        }
        return players;
    }

    @Override
    @Nullable
    public ManHuntPlayerView getRunner() {
        Iterator<ManHuntPlayerView> it = allMembers.get(ManHuntRole.RUNNER).iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<ManHuntPlayerView> getHunters() {
        return unmodifiableHunters;
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<ManHuntPlayerView> getSpectators() {
        return unmodifiableSpectators;
    }

    @Override
    @NotNull
    public GameRegion getRegion(@NotNull Environment environment) {
        return switch (environment) {
            case NORMAL -> getOverWorldRegion();
            case NETHER -> getNetherRegion();
            case THE_END -> getEndRegion();
            default -> throw new IllegalArgumentException(environment.toString());
        };
    }

    @Override
    @NotNull
    public GameRegion getOverWorldRegion() {
        if (overWorld == null) {
            throw new IllegalStateException("OverWorld region is not ready at state=" + state);
        }
        return overWorld;
    }

    @Override
    @NotNull
    public GameRegion getNetherRegion() {
        if (nether == null) {
            throw new IllegalStateException("Nether region is not ready at state=" + state);
        }
        return nether;
    }

    @Override
    @NotNull
    public GameRegion getEndRegion() {
        if (end == null) {
            throw new IllegalStateException("End is not ready at state=" + state);
        }
        return end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}

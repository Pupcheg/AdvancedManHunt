package me.supcheg.advancedmanhunt.game.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.PlayerUtil;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.util.ConcatenatedUnmodifiableCollection;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

class DefaultManHuntGame implements ManHuntGame {

    private final DefaultManHuntGameService gameService;

    private final UUID uniqueId;
    private final UUID owner;

    private final ManHuntGameConfiguration configuration;

    private final SetMultimap<ManHuntRole, UUID> allMembers;
    private final Set<UUID> unmodifiableHunters;
    private final Set<UUID> unmodifiableSpectators;
    private final Collection<UUID> unmodifiablePlayers;
    private final Collection<UUID> unmodifiableMembers;

    private final AtomicReference<GameState> state;

    // used after initialize
    private long startTime;
    private GameRegion overWorld;
    private GameRegion nether;
    private GameRegion end;
    private RegionPortalHandler portalHandler;
    private ImmutableLocation spawnLocation;
    private CountDownTimer safeLeaveTimer;
    private Set<CountDownTimer> timers;
    private Set<FreezeGroup> freezeGroups;
    private Map<Environment, ImmutableLocation> environment2runnerLastLocation;

    DefaultManHuntGame(@NotNull DefaultManHuntGameService gameService,
                       @NotNull UUID uniqueId, @NotNull UUID owner) {
        this.gameService = gameService;

        this.owner = owner;
        this.uniqueId = uniqueId;
        this.state = new AtomicReference<>(GameState.CREATE);

        this.configuration = new ManHuntGameConfiguration();

        this.allMembers = MultimapBuilder.enumKeys(ManHuntRole.class).hashSetValues().build();
        this.unmodifiableHunters = Collections.unmodifiableSet(allMembers.get(ManHuntRole.HUNTER));
        this.unmodifiableSpectators = Collections.unmodifiableSet(allMembers.get(ManHuntRole.SPECTATOR));
        this.unmodifiablePlayers = ConcatenatedUnmodifiableCollection.of(allMembers.get(ManHuntRole.HUNTER), allMembers.get(ManHuntRole.RUNNER));
        this.unmodifiableMembers = Collections.unmodifiableCollection(allMembers.values());
    }

    void setState(@NotNull GameState state) {
        Objects.requireNonNull(state, "state");

        GameState currentState = getState();
        if (state.lower(currentState)) {
            throw new IllegalStateException("Switching to lower state! " + currentState + " -> " + state + ", game: " + this);
        }
        this.state.setPlain(state);
    }

    @NotNull
    Map<Environment, ImmutableLocation> getEnvironmentToRunnerLastLocation() {
        if (environment2runnerLastLocation == null) {
            environment2runnerLastLocation = new EnumMap<>(Environment.class);
        }
        return environment2runnerLastLocation;
    }

    @NotNull
    Collection<CountDownTimer> getTimers() {
        return timers == null ? timers = new HashSet<>() : timers;
    }

    CountDownTimer getSafeLeaveTimer() {
        return safeLeaveTimer;
    }

    void setSafeLeaveTimer(CountDownTimer safeLeaveTimer) {
        this.safeLeaveTimer = safeLeaveTimer;
    }

    @NotNull
    Collection<FreezeGroup> getFreezeGroups() {
        return freezeGroups == null ? freezeGroups = new HashSet<>() : freezeGroups;
    }

    long getStartTime() {
        return startTime;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
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

    void setPortalHandler(@NotNull RegionPortalHandler portalHandler) {
        this.portalHandler = portalHandler;
    }

    RegionPortalHandler getPortalHandler() {
        return portalHandler;
    }

    @Override
    @Nullable
    public ImmutableLocation getSpawnLocation() {
        return spawnLocation;
    }

    void setSpawnLocation(@NotNull ImmutableLocation spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @NotNull
    SetMultimap<ManHuntRole, UUID> getAllMembers() {
        return allMembers;
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @Override
    @NotNull
    public GameState getState() {
        return state.getPlain();
    }

    @NotNull
    @Override
    public ManHuntGameConfiguration getConfig() {
        return configuration;
    }

    @Override
    public void start() {
        gameService.start(this);
    }

    @Override
    public void stop(@Nullable ManHuntRole winnerRole) {
        gameService.stop(this, winnerRole);
    }

    @Override
    @Nullable
    @CanIgnoreReturnValue
    public ManHuntRole addMember(@NotNull UUID uniqueId) {
        ManHuntRole returnRole = null;

        if (getState() != GameState.CREATE) {
            if (!allMembers.containsValue(uniqueId)) {
                if (ManHuntRole.SPECTATOR.canJoin(this)) {
                    allMembers.put(ManHuntRole.SPECTATOR, uniqueId);
                    returnRole = ManHuntRole.SPECTATOR;
                }
            }
        } else {
            if (!allMembers.containsValue(uniqueId)) {
                for (ManHuntRole role : ManHuntRole.VALUES) {
                    if (role.canJoin(this)) {
                        allMembers.put(role, uniqueId);
                        returnRole = role;
                        break;
                    }
                }
            }
        }
        return returnRole;
    }

    @Override
    @Nullable
    public ManHuntRole getRole(@NotNull UUID uniqueId) {
        ManHuntRole playerRole = null;
        for (ManHuntRole role : ManHuntRole.VALUES) {
            if (role.getPlayers(this).contains(uniqueId)) {
                playerRole = role;
                break;
            }
        }
        return playerRole;
    }

    @Override
    @CanIgnoreReturnValue
    public boolean addMember(@NotNull UUID uniqueId, @NotNull ManHuntRole role) {
        if (role != ManHuntRole.SPECTATOR && getState() != GameState.CREATE) {
            throw new IllegalStateException("Unable to add players to a game already started");
        }

        if (role.canJoin(this)) {
            allMembers.put(role, uniqueId);
            return true;
        }
        return false;
    }

    @Override
    public boolean canAcceptPlayer() {
        return getState() == GameState.CREATE &&
                (ManHuntRole.RUNNER.canJoin(this) || ManHuntRole.HUNTER.canJoin(this));
    }

    @Override
    public boolean canAcceptSpectator() {
        return ManHuntRole.SPECTATOR.canJoin(this);
    }

    @Override
    public boolean canStart() {
        return getState() == GameState.CREATE
                && PlayerUtil.isAnyOnline(allMembers.get(ManHuntRole.RUNNER))
                && PlayerUtil.isAnyOnline(allMembers.get(ManHuntRole.HUNTER));
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<UUID> getMembers() {
        return unmodifiableMembers;
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Collection<UUID> getPlayers() {
        return unmodifiablePlayers;
    }

    @Override
    @Nullable
    public UUID getRunner() {
        Iterator<UUID> it = allMembers.get(ManHuntRole.RUNNER).iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Set<UUID> getHunters() {
        return unmodifiableHunters;
    }

    @Override
    @NotNull
    @UnmodifiableView
    public Set<UUID> getSpectators() {
        return unmodifiableSpectators;
    }

    @Override
    @NotNull
    public GameRegion getOverWorldRegion() {
        if (overWorld == null) {
            throw new IllegalStateException("OverWorld region is not ready at state=" + getState());
        }
        return overWorld;
    }

    @Override
    @NotNull
    public GameRegion getNetherRegion() {
        if (nether == null) {
            throw new IllegalStateException("Nether region is not ready at state=" + getState());
        }
        return nether;
    }

    @Override
    @NotNull
    public GameRegion getEndRegion() {
        if (end == null) {
            throw new IllegalStateException("End is not ready at state=" + getState());
        }
        return end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}

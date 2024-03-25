package me.supcheg.advancedmanhunt.game.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import me.supcheg.advancedmanhunt.action.RunningAction;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.SafeLeaveHandler;
import me.supcheg.advancedmanhunt.gui.ConfigurateGameGui;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.Players;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.util.OtherCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
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

class DefaultManHuntGame implements ManHuntGame {

    private final DefaultManHuntGameService service;

    private final UUID uniqueId;
    private final UUID owner;

    private final ManHuntGameConfiguration configuration;

    private final SetMultimap<ManHuntRole, UUID> allMembers;
    private final Set<UUID> unmodifiableHunters;
    private final Set<UUID> unmodifiableSpectators;
    private final Collection<UUID> unmodifiablePlayers;
    private final Collection<UUID> unmodifiableMembers;

    private volatile GameState state;
    private ConfigurateGameGui configurationGui;

    // used after initialize
    @Getter
    private long startTime;
    private GameRegion overworld;
    private GameRegion nether;
    private GameRegion end;
    private RegionPortalHandler portalHandler;
    private SafeLeaveHandler safeLeaveHandler;
    private ImmutableLocation spawnLocation;
    private final Set<CountDownTimer> timers;
    private final Set<FreezeGroup> freezeGroups;
    private final Map<RealEnvironment, ImmutableLocation> environment2runnerLastLocation;


    DefaultManHuntGame(@NotNull DefaultManHuntGameService service, @NotNull UUID uniqueId, @NotNull UUID owner) {
        this.service = service;

        this.owner = owner;
        this.uniqueId = uniqueId;
        this.state = GameState.CREATE;

        this.configuration = new ManHuntGameConfiguration();

        this.allMembers = MultimapBuilder.enumKeys(ManHuntRole.class).hashSetValues().build();
        this.unmodifiableHunters = Collections.unmodifiableSet(allMembers.get(ManHuntRole.HUNTER));
        this.unmodifiableSpectators = Collections.unmodifiableSet(allMembers.get(ManHuntRole.SPECTATOR));
        this.unmodifiablePlayers = OtherCollections.concat(
                allMembers.get(ManHuntRole.HUNTER),
                allMembers.get(ManHuntRole.RUNNER)
        );
        this.unmodifiableMembers = Collections.unmodifiableCollection(allMembers.values());

        this.timers = new HashSet<>();
        this.freezeGroups = new HashSet<>();
        this.environment2runnerLastLocation = new EnumMap<>(RealEnvironment.class);
    }

    void setState(@NotNull GameState state) {
        Objects.requireNonNull(state, "state");
        this.state = state;
    }

    @NotNull
    Map<RealEnvironment, ImmutableLocation> getEnvironmentToRunnerLastLocation() {
        return environment2runnerLastLocation;
    }

    @NotNull
    Collection<CountDownTimer> getTimers() {
        return timers;
    }

    SafeLeaveHandler getSafeLeaveHandler() {
        return safeLeaveHandler;
    }

    void setSafeLeaveHandler(SafeLeaveHandler safeLeaveHandler) {
        this.safeLeaveHandler = safeLeaveHandler;
    }

    @NotNull
    Collection<FreezeGroup> getFreezeGroups() {
        return freezeGroups;
    }

    void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    void setOverworldRegion(GameRegion overworld) {
        this.overworld = overworld;
    }

    void setNetherRegion(GameRegion nether) {
        this.nether = nether;
    }

    void setEndRegion(GameRegion end) {
        this.end = end;
    }

    void setPortalHandler(RegionPortalHandler portalHandler) {
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
        return state;
    }

    @NotNull
    @Override
    public ManHuntGameConfiguration getConfig() {
        return configuration;
    }

    @NotNull
    @Override
    public RunningAction start() {
        return service.start(this);
    }

    @Override
    public void stop(@Nullable ManHuntRole winnerRole) {
        service.stop(this, winnerRole);
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
                for (ManHuntRole role : ManHuntRole.allManHuntRoles()) {
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
        for (ManHuntRole role : ManHuntRole.allManHuntRoles()) {
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
                && Players.isAnyOnline(allMembers.get(ManHuntRole.RUNNER))
                && Players.isAnyOnline(allMembers.get(ManHuntRole.HUNTER));
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
    @UnknownNullability
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

    @UnknownNullability
    @Override
    public GameRegion getOverworld() {
        return overworld;
    }

    @UnknownNullability
    @Override
    public GameRegion getNether() {
        return nether;
    }

    @UnknownNullability
    @Override
    public GameRegion getEnd() {
        return end;
    }

    @NotNull
    @Override
    public ConfigurateGameGui getConfigGui() {
        if (configurationGui == null) {
            configurationGui = service.createConfigGui(this);

        }
        return configurationGui;
    }

    public void unregisterConfigGui() {
        if (configurationGui != null) {
            service.unregisterConfigGui(configurationGui);
            configurationGui = null;
        }
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}

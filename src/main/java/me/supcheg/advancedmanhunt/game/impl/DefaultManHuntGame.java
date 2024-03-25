package me.supcheg.advancedmanhunt.game.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.supcheg.advancedmanhunt.action.RunningAction;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.SafeLeaveHandler;
import me.supcheg.advancedmanhunt.gui.ConfigurateGameGui;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.util.OtherCollections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
class DefaultManHuntGame implements ManHuntGame {

    @Getter(AccessLevel.NONE)
    private final DefaultManHuntGameStarter starter;

    private final UUID uniqueId;
    private final UUID owner;

    private final ManHuntGameConfiguration config;

    private final SetMultimap<ManHuntRole, UUID> allMembers;
    private final Set<UUID> runnerAsCollection;
    private final Set<UUID> hunters;
    private final Set<UUID> spectators;
    private final Collection<UUID> players;
    private final Collection<UUID> members;

    private volatile GameState state;
    @Setter(AccessLevel.NONE)
    private ConfigurateGameGui configGui;

    // used after initialize
    private long startTime;
    private GameRegion overworld;
    private GameRegion nether;
    private GameRegion end;
    private RegionPortalHandler portalHandler;
    private SafeLeaveHandler safeLeaveHandler;
    private ImmutableLocation spawnLocation;
    private final Set<CountDownTimer> timers;
    private final Set<FreezeGroup> freezeGroups;
    private final Map<RealEnvironment, ImmutableLocation> environmentToRunnerLastLocation;

    DefaultManHuntGame(@NotNull DefaultManHuntGameStarter starter, @NotNull UUID uniqueId, @NotNull UUID owner) {
        this.starter = starter;

        this.owner = owner;
        this.uniqueId = uniqueId;
        this.state = GameState.CREATE;

        this.config = new ManHuntGameConfiguration();

        this.allMembers = MultimapBuilder.enumKeys(ManHuntRole.class).hashSetValues().build();
        this.runnerAsCollection = Collections.unmodifiableSet(allMembers.get(ManHuntRole.RUNNER));
        this.hunters = Collections.unmodifiableSet(allMembers.get(ManHuntRole.HUNTER));
        this.spectators = Collections.unmodifiableSet(allMembers.get(ManHuntRole.SPECTATOR));
        this.players = OtherCollections.concat(runnerAsCollection, hunters);
        this.members = Collections.unmodifiableCollection(allMembers.values());

        this.timers = new HashSet<>();
        this.freezeGroups = new HashSet<>();
        this.environmentToRunnerLastLocation = new EnumMap<>(RealEnvironment.class);
    }

    @NotNull
    @Override
    public RunningAction start() {
        return starter.start(this);
    }

    @Override
    public void stop(@Nullable ManHuntRole winnerRole) {
        starter.stop(this, winnerRole);
    }

    void setState(@NotNull GameState state) {
        Objects.requireNonNull(state, "state");
        this.state = state;
    }

    @Nullable
    @Override
    public ManHuntRole addMember(@NotNull UUID uniqueId) {
        if (allMembers.containsValue(uniqueId)) {
            return null;
        }

        if (state != GameState.CREATE) {
            if (ManHuntRole.SPECTATOR.canJoin(this)) {
                allMembers.put(ManHuntRole.SPECTATOR, uniqueId);
                return ManHuntRole.SPECTATOR;
            }
        } else {
            for (ManHuntRole role : ManHuntRole.allManHuntRoles()) {
                if (role.canJoin(this)) {
                    allMembers.put(role, uniqueId);
                    return role;
                }
            }
        }

        return null;
    }

    @Override
    public boolean addMember(@NotNull UUID uniqueId, @NotNull ManHuntRole role) {
        if (role != ManHuntRole.SPECTATOR && state != GameState.CREATE) {
            throw new IllegalStateException("Unable to add players to a already started game");
        }

        if (role.canJoin(this)) {
            allMembers.put(role, uniqueId);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ManHuntRole getRole(@NotNull UUID uniqueId) {
        for (ManHuntRole role : ManHuntRole.allManHuntRoles()) {
            if (role.getPlayers(this).contains(uniqueId)) {
                return role;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public ConfigurateGameGui getConfigInterface() {
        return configGui == null ? (configGui = starter.createConfigInterface(this)) : configGui;
    }

    void unregisterConfigGui() {
        if (configGui != null) {
            starter.unregisterConfigGui(configGui);
            configGui = null;
        }
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}

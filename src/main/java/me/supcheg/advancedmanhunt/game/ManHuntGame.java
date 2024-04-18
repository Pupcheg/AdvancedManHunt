package me.supcheg.advancedmanhunt.game;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.Setter;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.game.handler.ManHuntGameHandler;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.util.OtherCollections;
import me.supcheg.advancedmanhunt.util.Unchecked;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Getter
@Setter
public class ManHuntGame {

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

    // used after initialize
    private GameRegion overworld;
    private GameRegion nether;
    private GameRegion end;
    private ImmutableLocation spawnLocation;
    private final Map<Class<?>, ManHuntGameHandler> handlers;
    private final Set<CountDownTimer> timers;
    private final Set<FreezeGroup> freezeGroups;

    public ManHuntGame(@NotNull UUID selfUniqueId, @NotNull UUID ownerUniqueId) {
        this.owner = ownerUniqueId;
        this.uniqueId = selfUniqueId;
        this.state = GameState.CREATE;

        this.config = new ManHuntGameConfiguration();

        this.allMembers = MultimapBuilder.enumKeys(ManHuntRole.class).hashSetValues().build();
        this.runnerAsCollection = Collections.unmodifiableSet(allMembers.get(ManHuntRole.RUNNER));
        this.hunters = Collections.unmodifiableSet(allMembers.get(ManHuntRole.HUNTER));
        this.spectators = Collections.unmodifiableSet(allMembers.get(ManHuntRole.SPECTATOR));
        this.players = OtherCollections.concat(runnerAsCollection, hunters);
        this.members = Collections.unmodifiableCollection(allMembers.values());

        this.handlers = new HashMap<>();
        this.timers = new HashSet<>();
        this.freezeGroups = new HashSet<>();
    }

    public void setState(@NotNull GameState state) {
        Objects.requireNonNull(state, "state");
        this.state = state;
    }

    public void registerHandler(@NotNull Function<ManHuntGame, ManHuntGameHandler> function) {
        ManHuntGameHandler handler = function.apply(this);
        handlers.put(handler.getClass(), handler);
        handler.register();
    }

    @Nullable
    public <T extends ManHuntGameHandler> T getNullableHandler(@NotNull Class<T> handlerClass) {
        return Unchecked.uncheckedCast(handlers.get(handlerClass));
    }

    @NotNull
    public <T extends ManHuntGameHandler> T getHandler(@NotNull Class<T> handlerClass) {
        return Objects.requireNonNull(getNullableHandler(handlerClass), () -> "handler with type=" + handlerClass);
    }

    @CanIgnoreReturnValue
    public boolean unregisterHandler(@NotNull Class<? extends ManHuntGameHandler> handlerClass) {
        ManHuntGameHandler handler = handlers.remove(handlerClass);
        if (handler != null) {
            handler.unregister();
            return true;
        }
        return false;
    }

    @Nullable
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
    public ManHuntRole getRole(@NotNull UUID uniqueId) {
        for (ManHuntRole role : ManHuntRole.allManHuntRoles()) {
            if (role.getPlayers(this).contains(uniqueId)) {
                return role;
            }
        }
        return null;
    }

    public boolean hasRole(@NotNull UUID uniqueId, @NotNull ManHuntRole expected) {
        return expected.getPlayers(this).contains(uniqueId);
    }

    @UnknownNullability
    public UUID getRunner() {
        Iterator<UUID> it = getRunnerAsCollection().iterator();
        return it.hasNext() ? it.next() : null;
    }

    @UnknownNullability
    public GameRegion getRegion(@NotNull RealEnvironment environment) {
        return switch (environment) {
            case OVERWORLD -> getOverworld();
            case NETHER -> getNether();
            case THE_END -> getEnd();
        };
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }
}

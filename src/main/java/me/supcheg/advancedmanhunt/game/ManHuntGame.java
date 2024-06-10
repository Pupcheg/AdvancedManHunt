package me.supcheg.advancedmanhunt.game;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import lombok.Setter;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.game.handler.ManHuntGameHandler;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.timer.CountDownTimer;
import me.supcheg.advancedmanhunt.util.Unchecked;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.HashSet;
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

    private final ManHuntGameMembers members;

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
        this.members = new ManHuntGameMembers();
        this.config = new ManHuntGameConfiguration();
        this.handlers = new HashMap<>();
        this.timers = new HashSet<>();
        this.freezeGroups = new HashSet<>();
    }

    @NotNull
    public ManHuntGameMembers members() {
        return members;
    }

    public void setState(@NotNull GameState state) {
        Objects.requireNonNull(state, "state");
        this.state = state;
    }

    public void registerHandler(@NotNull EventListenerRegistry registry,
                                @NotNull Function<ManHuntGame, ManHuntGameHandler> function) {
        ManHuntGameHandler handler = function.apply(this);
        handlers.put(handler.getClass(), handler);
        handler.registerWith(registry);
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
        if (members.all().uniqueIds().contains(uniqueId)) {
            return null;
        }

        if (state != GameState.CREATE) {
            if (canAccept(ManHuntRole.SPECTATOR)) {
                members.spectators().uniqueIds().add(uniqueId);
                return ManHuntRole.SPECTATOR;
            }
        } else {
            for (ManHuntRole role : ManHuntRole.values()) {
                if (canAccept(role)) {
                    members.forRole(role).uniqueIds().add(uniqueId);
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

        if (!canAccept(role)) {
            return false;
        }

        return members.forRole(role).uniqueIds().add(uniqueId);
    }

    public boolean canAccept(@NotNull ManHuntRole role) {
        return switch (role) {
            case RUNNER -> members.runners().uniqueIds().isEmpty();
            case HUNTER -> members.hunters().uniqueIds().size() < config.maxHunters();
            case SPECTATOR -> members.spectators().uniqueIds().size() < config.maxSpectators();
        };
    }

    public boolean hasRole(@NotNull UUID uniqueId, @NotNull ManHuntRole expected) {
        return members.forRole(expected).uniqueIds().contains(uniqueId);
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

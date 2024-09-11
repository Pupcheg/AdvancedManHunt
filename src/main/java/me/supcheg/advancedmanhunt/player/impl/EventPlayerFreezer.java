package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistration;
import me.supcheg.advancedmanhunt.event.registry.EventListenerRegistry;
import me.supcheg.advancedmanhunt.player.FreezeGroup;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EventPlayerFreezer implements Listener, PlayerFreezer, AutoCloseable {
    private final SetMultimap<UUID, FreezeGroup> player2groups;
    private final EventListenerRegistration listenerRegistration;

    @Inject
    public EventPlayerFreezer(@NotNull EventListenerRegistry listenerRegistry) {
        this.player2groups = Multimaps.synchronizedSetMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
        this.listenerRegistration = listenerRegistry.register(this);
    }

    @Override
    public void close() {
        listenerRegistration.unregister();
    }

    @Override
    public boolean isFrozen(@NotNull UUID uniqueId) {
        return player2groups.containsKey(uniqueId);
    }

    @Override
    @NotNull
    public FreezeGroup newFreezeGroup() {
        return new DefaultFreezeGroup();
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    class DefaultFreezeGroup implements FreezeGroup {
        private final Set<UUID> players;

        DefaultFreezeGroup() {
            this(new HashSet<>());
        }

        @Override
        public void add(@NotNull UUID uniqueId) {
            player2groups.put(uniqueId, this);
            players.add(uniqueId);
        }

        @Override
        public void remove(@NotNull UUID uniqueId) {
            player2groups.remove(uniqueId, this);
            players.remove(uniqueId);
        }

        @Override
        public void clear() {
            players.forEach(uniqueId -> player2groups.remove(uniqueId, this));
            players.clear();
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (isFrozen(event.getPlayer().getUniqueId()) && notEqualsXYZ(event.getFrom(), event.getTo())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && isFrozen(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    private boolean notEqualsXYZ(@NotNull Location loc1, @NotNull Location loc2) {
        return Double.doubleToLongBits(loc1.getX()) != Double.doubleToLongBits(loc2.getX()) ||
               Double.doubleToLongBits(loc1.getY()) != Double.doubleToLongBits(loc2.getY()) ||
               Double.doubleToLongBits(loc1.getZ()) != Double.doubleToLongBits(loc2.getZ());
    }
}

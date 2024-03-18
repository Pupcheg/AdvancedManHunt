package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultPlayerFreezer implements Listener, PlayerFreezer {

    private final FreezeGroup dummyFreezeGroup;
    private final SetMultimap<UUID, FreezeGroup> player2groups;

    public DefaultPlayerFreezer(@NotNull EventListenerRegistry eventListenerRegistry) {
        this.dummyFreezeGroup = new DefaultFreezeGroup(Collections.emptySet());
        this.player2groups = Multimaps.synchronizedSetMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
        eventListenerRegistry.addListener(this);
    }

    @Override
    public void freeze(@NotNull UUID uniqueId) {
        player2groups.put(uniqueId, dummyFreezeGroup);
    }

    @Override
    public void unfreeze(@NotNull UUID uniqueId) {
        player2groups.remove(uniqueId, dummyFreezeGroup);
    }

    @Override
    public boolean isFrozen(@NotNull UUID uniqueId) {
        return player2groups.containsKey(uniqueId);
    }


    @Override
    @NotNull
    public FreezeGroup newFreezeGroup() {
        return new DefaultFreezeGroup(new HashSet<>());
    }

    @RequiredArgsConstructor
    class DefaultFreezeGroup implements FreezeGroup {
        private final Set<UUID> players;

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
            for (UUID player : players) {
                player2groups.remove(player, this);
            }
            players.clear();
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        if (isFrozen(event.getPlayer()) && notEqualsXYZ(event.getFrom(), event.getTo())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (isFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NotNull EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && isFrozen(player)) {
            event.setCancelled(true);
        }
    }

    private boolean notEqualsXYZ(@NotNull Location loc1, @NotNull Location loc2) {
        return Double.doubleToLongBits(loc1.getX()) != Double.doubleToLongBits(loc2.getX()) ||
                Double.doubleToLongBits(loc1.getY()) != Double.doubleToLongBits(loc2.getY()) ||
                Double.doubleToLongBits(loc1.getZ()) != Double.doubleToLongBits(loc2.getZ());
    }
}

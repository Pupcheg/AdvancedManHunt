package me.supcheg.advancedmanhunt.player.freeze.impl;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.player.freeze.FreezeGroup;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
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
        this.player2groups = MultimapBuilder.hashKeys().hashSetValues().build();
        eventListenerRegistry.addListener(this);
    }

    @Override
    public void freeze(@NotNull Player player) {
        player2groups.put(player.getUniqueId(), dummyFreezeGroup);
    }

    @Override
    public void unfreeze(@NotNull Player player) {
        player2groups.remove(player.getUniqueId(), dummyFreezeGroup);
    }

    @Override
    public boolean isFrozen(@NotNull Player player) {
        return player2groups.containsKey(player.getUniqueId());
    }


    @Override
    @NotNull
    public DefaultFreezeGroup newFreezeGroup() {
        return new DefaultFreezeGroup(new HashSet<>());
    }

    @AllArgsConstructor
    class DefaultFreezeGroup implements FreezeGroup {
        private final Set<UUID> players;

        @Override
        public void add(@NotNull Player player) {
            player2groups.put(player.getUniqueId(), this);
            players.add(player.getUniqueId());
        }

        @Override
        public void remove(@NotNull Player player) {
            player2groups.remove(player.getUniqueId(), this);
            players.remove(player.getUniqueId());
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

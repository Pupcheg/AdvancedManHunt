package me.supcheg.advancedmanhunt.player.freeze.impl;

import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultPlayerFreezer implements Listener, PlayerFreezer {

    private final Set<UUID> playersToFreeze;

    public DefaultPlayerFreezer(@NotNull AdvancedManHuntPlugin plugin) {
        this.playersToFreeze = new HashSet<>();
        plugin.addListener(this);
    }

    @Override
    public void freeze(@NotNull Player player) {
        playersToFreeze.add(player.getUniqueId());
    }

    @Override
    public void unfreeze(@NotNull Player player) {
        playersToFreeze.remove(player.getUniqueId());
    }

    @Override
    public boolean isFrozen(@NotNull Player player) {
        return playersToFreeze.contains(player.getUniqueId());
    }

    //
    // Groups
    //

    @Override@NotNull
    public DefaultPlayerFreezer.DefaultFreezeGroup newFreezeGroup() {
        return new DefaultFreezeGroup(new HashSet<>());
    }

    @AllArgsConstructor
    public class DefaultFreezeGroup implements FreezeGroup {
        private final Set<UUID> players;

        @Override
        public void add(@NotNull Player player) {
            players.add(player.getUniqueId());
            playersToFreeze.add(player.getUniqueId());
        }

        @Override
        public void remove(@NotNull Player player) {
            players.remove(player.getUniqueId());
            playersToFreeze.remove(player.getUniqueId());
        }

        @Override
        public void clear() {
            playersToFreeze.removeAll(players);
        }
    }

    //
    // Listener
    //

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

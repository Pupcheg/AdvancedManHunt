package me.supcheg.advancedmanhunt.game.handler;

import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.RealEnvironment;
import me.supcheg.advancedmanhunt.text.MessageText;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;
import static me.supcheg.advancedmanhunt.region.RealEnvironment.environment;

@Slf4j
public class ManHuntGameCompassHandler extends ManHuntGameHandler {
    private final Map<RealEnvironment, ImmutableLocation> environmentToLastLocation =
            new EnumMap<>(RealEnvironment.class);

    public ManHuntGameCompassHandler(@NotNull ManHuntGame game) {
        super(game);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleHunterInteract(@NotNull PlayerInteractEvent event) {
        Player hunter = event.getPlayer();

        if (!shouldHandleAt(hunter.getLocation()) || !isPlaying()
            || game.hasRole(hunter.getUniqueId(), ManHuntRole.HUNTER)) {
            return;
        }

        ItemStack itemStack = event.getItem();
        if (itemStack != null && itemStack.getType() == Material.COMPASS) {
            event.setCancelled(true);

            UUID runnerUniqueId = game.members().runner();
            Objects.requireNonNull(runnerUniqueId);

            Player runner = Bukkit.getPlayer(runnerUniqueId);

            String runnerName =
                    runner == null ?
                            Objects.requireNonNull(Bukkit.getOfflinePlayer(runnerUniqueId).getName(), "runner's name") :
                            runner.getName();

            RealEnvironment hunterEnvironment = environment(hunter.getWorld());
            Location runnerLocation =
                    runner == null || environment(runner.getWorld()) != hunterEnvironment ?
                            position(environmentToLastLocation.get(hunterEnvironment)).bukkitLocation() :
                            runner.getLocation();


            CompassMeta meta = (CompassMeta) itemStack.getItemMeta();
            meta.setLodestoneTracked(false);
            meta.setLodestone(runnerLocation);
            itemStack.setItemMeta(meta);

            MessageText.COMPASS_USE.send(hunter, runnerName);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleRunnerTeleport(@NotNull PlayerTeleportEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        if (shouldHandleAt(event.getPlayer().getLocation()) && isPlaying()
            && game.hasRole(playerUniqueId, ManHuntRole.RUNNER)) {
            RealEnvironment fromEnvironment = environment(event.getFrom().getWorld());
            RealEnvironment toEnvironment = environment(event.getTo().getWorld());

            if (fromEnvironment != toEnvironment) {
                environmentToLastLocation
                        .put(fromEnvironment, position(event.getFrom()).immutableLocation());
            }
        }
    }

    @EventHandler
    public void handleRunnerQuit(@NotNull PlayerQuitEvent event) {
        UUID playerUniqueId = event.getPlayer().getUniqueId();

        Location playerLocation = event.getPlayer().getLocation();
        if (shouldHandleAt(playerLocation) && isPlaying() && game.hasRole(playerUniqueId, ManHuntRole.RUNNER)) {
            environmentToLastLocation
                    .put(environment(playerLocation.getWorld()), position(playerLocation).immutableLocation());
        }
    }
}

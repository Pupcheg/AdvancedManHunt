package me.supcheg.advancedmanhunt.player.impl;

import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.util.LocationParser;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeleportingPlayerReturner implements PlayerReturner {

    private Location location;

    @Override
    public void returnPlayer(@NotNull Player player) {
        if (location == null) {
            location = LocationParser.parseLocation(AdvancedManHuntConfig.Game.PlayerReturner.ARGUMENT);
        }
        player.teleport(location);
    }
}

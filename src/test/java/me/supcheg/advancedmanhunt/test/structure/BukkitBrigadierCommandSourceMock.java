package me.supcheg.advancedmanhunt.test.structure;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BukkitBrigadierCommandSourceMock implements BukkitBrigadierCommandSource {

    private final Player player;

    public static BukkitBrigadierCommandSourceMock of(@NotNull Player player) {
        return new BukkitBrigadierCommandSourceMock(player);
    }

    @NotNull
    @Override
    public Entity getBukkitEntity() {
        return player;
    }

    @NotNull
    @Override
    public World getBukkitWorld() {
        return player.getWorld();
    }

    @NotNull
    @Override
    public Location getBukkitLocation() {
        return player.getLocation();
    }

    @Override
    public CommandSender getBukkitSender() {
        return player;
    }
}

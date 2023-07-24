package me.supcheg.advancedmanhunt.structure;

import be.seeseemelk.mockbukkit.command.MessageTarget;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BukkitBrigadierCommandSourceMock implements BukkitBrigadierCommandSource, MessageTarget {

    private final PlayerMock player;

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static BukkitBrigadierCommandSourceMock of(@NotNull PlayerMock player) {
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

    @Nullable
    @Override
    public Component nextComponentMessage() {
        return player.nextComponentMessage();
    }
}

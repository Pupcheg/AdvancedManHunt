package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.base.Suppliers;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.util.PositionAdapters;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TeleportingPlayerReturner implements PlayerReturner {

    private final String rawLocation;
    private final Supplier<Location> locationSupplier;

    public TeleportingPlayerReturner(@NotNull String rawLocation) {
        this.rawLocation = rawLocation;
        this.locationSupplier = Suppliers.memoize(this::loadLocation);
    }

    @SneakyThrows
    @NotNull
    private Location loadLocation() {
        return PositionAdapters.bukkitLocation().deserialize(rawLocation);
    }

    @Override
    public void returnPlayer(@NotNull Player player) {
        player.teleport(locationSupplier.get());
    }
}

package me.supcheg.advancedmanhunt.player.impl;

import com.google.common.base.Suppliers;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.coord.ImmutableLocations;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TeleportingPlayerReturner implements PlayerReturner {

    private final Supplier<ImmutableLocation> locationSupplier;

    public TeleportingPlayerReturner(@NotNull String rawLocation) {
        locationSupplier = Suppliers.memoize(() -> ImmutableLocations.parseLocation(rawLocation));
    }

    @Override
    public void returnPlayer(@NotNull Player player) {
        player.teleport(locationSupplier.get().asMutable());
    }
}

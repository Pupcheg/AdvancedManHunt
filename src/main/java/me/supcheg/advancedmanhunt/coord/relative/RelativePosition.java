package me.supcheg.advancedmanhunt.coord.relative;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.coord.ImmutableLocation.immutableLocation;

@SuppressWarnings("UnstableApiUsage")
public interface RelativePosition {
    @NotNull
    RelativePositionSource source();

    @NotNull
    Position relative();

    @NotNull
    default Location bukkitRelative() {
        Position pos = relative();
        return new Location(source().world().getWorld(), pos.x(), pos.y(), pos.z(), 0, 0);
    }

    @NotNull
    default ImmutableLocation immutableRelative() {
        Position pos = relative();
        return immutableLocation()
                .world(source().world())
                .xyz(pos)
                .build();
    }

    @NotNull
    Position absolute();

    @NotNull
    default Location bukkitAbsolute() {
        Position pos = absolute();
        return new Location(source().world().getWorld(), pos.x(), pos.y(), pos.z(), 0, 0);
    }

    @NotNull
    default ImmutableLocation immutableAbsolute() {
        Position pos = absolute();
        return immutableLocation()
                .world(source().world())
                .xyz(pos)
                .build();
    }
}

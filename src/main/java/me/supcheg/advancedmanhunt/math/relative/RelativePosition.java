package me.supcheg.advancedmanhunt.math.relative;

import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RelativePosition {

    @NotNull
    static RelativePositionSource world(@NotNull WorldReference world) {
        return new WorldRelativePositionSource(world);
    }

    @NotNull
    RelativePositionSource source();

    @NotNull
    default Position relative() {
        return builderRelative().finePosition();
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    PositionBuilder builderRelative();

    @NotNull
    default Location bukkitRelative() {
        return builderRelative().bukkitLocation();
    }

    @NotNull
    default ImmutableLocation immutableRelative() {
        return builderRelative().immutableLocation();
    }

    @NotNull
    default Position absolute() {
        return builderAbsolute().finePosition();
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    PositionBuilder builderAbsolute();

    @NotNull
    default Location bukkitAbsolute() {
        return builderAbsolute().bukkitLocation();
    }

    @NotNull
    default ImmutableLocation immutableAbsolute() {
        return builderAbsolute().immutableLocation();
    }
}

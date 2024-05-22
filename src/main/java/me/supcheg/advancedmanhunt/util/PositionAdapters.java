package me.supcheg.advancedmanhunt.util;

import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PositionAdapters {
    private static final PositionAdapter<ImmutableLocation> IMMUTABLE_LOCATION =
            new PositionAdapter<>(ImmutableLocation.class, PositionBuilder::immutableLocation);
    private static final PositionAdapter<Location> BUKKIT_LOCATION =
            new PositionAdapter<>(Location.class, PositionBuilder::bukkitLocation);
    private static final PositionAdapter<Position> POSITION_ADAPTER =
            new PositionAdapter<>(Position.class, PositionBuilder::finePosition);

    @NotNull
    @Contract(pure = true)
    public static PositionAdapter<ImmutableLocation> immutableLocation() {
        return IMMUTABLE_LOCATION;
    }

    @NotNull
    @Contract(pure = true)
    public static PositionAdapter<Location> bukkitLocation() {
        return BUKKIT_LOCATION;
    }

    @NotNull
    @Contract(pure = true)
    public static PositionAdapter<Position> position() {
        return POSITION_ADAPTER;
    }
}

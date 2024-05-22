package me.supcheg.advancedmanhunt.math.builder;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PositionBuilder extends FinePosition, PositionBuilderApplicable {

    @NotNull
    @Contract("-> new")
    static PositionBuilder position() {
        return new DefaultPositionBuilder();
    }

    @NotNull
    @Contract("_ -> new")
    static PositionBuilder position(@NotNull Position position) {
        return position().apply(position);
    }

    @NotNull
    @Contract("_ -> this")
    default PositionBuilder world(@Nullable World world) {
        return world(WorldReference.ofNullable(world));
    }

    @NotNull
    @Contract("_ -> this")
    PositionBuilder world(@Nullable WorldReference worldReference);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder xyz(@NotNull Position pos);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder offset(@NotNull Position pos);

    @NotNull
    @Contract("_, _, _ -> this")
    PositionBuilder xyz(double x, double y, double z);

    @NotNull
    @Contract("-> this")
    PositionBuilder negateXYZ();

    @NotNull
    @Contract("-> this")
    PositionBuilder negateYawPitch();

    @NotNull
    @Contract("_, _, _ -> this")
    PositionBuilder offset(double x, double y, double z);

    @Nullable
    @Contract(pure = true)
    WorldReference world();

    @Contract(pure = true)
    float yaw();

    @Contract(pure = true)
    float pitch();

    @NotNull
    @Contract("_ -> this")
    PositionBuilder x(double x);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder y(double y);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder z(double z);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder yaw(float yaw);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder pitch(float pitch);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder yaw(double yaw);

    PositionBuilder pitch(double pitch);

    @NotNull
    @Contract("_ -> this")
    PositionBuilder apply(@NotNull Position position);

    @NotNull
    @Contract(pure = true)
    ImmutableLocation immutableLocation();

    @NotNull
    @Contract(pure = true)
    Location bukkitLocation();

    @NotNull
    @Contract(pure = true)
    FinePosition finePosition();

    @NotNull
    @Contract(pure = true)
    BlockPosition blockPosition();

    @NotNull
    @Contract(value = "-> new", pure = true)
    PositionBuilder clone();
}

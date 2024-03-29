package me.supcheg.advancedmanhunt.coord;

import io.papermc.paper.math.FinePosition;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class ImmutableLocation implements FinePosition {
    private final WorldReference worldReference;
    private final double x;
    private final double y;
    private final double z;

    private final float yaw;
    private final float pitch;

    public ImmutableLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        this(world == null ? null : WorldReference.of(world), x, y, z, yaw, pitch);
    }

    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static ImmutableLocation copyOf(@Nullable Location location) {
        if (location == null) {
            return null;
        }

        return new ImmutableLocation(
                location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch()
        );
    }

    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static Location asMutable(@Nullable ImmutableLocation immutableLocation) {
        return immutableLocation == null ? null : immutableLocation.asMutable();
    }

    @Nullable
    public World getWorld() {
        return worldReference == null ? null : worldReference.getWorld();
    }

    public int getBlockX() {
        return blockX();
    }

    public int getBlockY() {
        return blockY();
    }

    public int getBlockZ() {
        return blockZ();
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public Location asMutable() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public ImmutableLocation withWorld(@Nullable World world) {
        return new ImmutableLocation(world, x, y, z, yaw, pitch);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public ImmutableLocation plus(@NotNull KeyedCoord coord) {
        return new ImmutableLocation(worldReference, this.x + coord.getX(), this.y + y, this.z + coord.getZ(), yaw, pitch);
    }
}

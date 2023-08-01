package me.supcheg.advancedmanhunt.coord;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ImmutableLocation extends Location {
    public ImmutableLocation(@Nullable World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public ImmutableLocation(@Nullable World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    @Nullable
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static ImmutableLocation copyOf(@Nullable Location location) {
        if (location == null) {
            return null;
        }
        if (location instanceof ImmutableLocation immutableLocation) {
            return immutableLocation;
        }

        return new ImmutableLocation(
                location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch()
        );
    }

    @Deprecated
    @Nullable
    @Contract("_ -> param1")
    public static ImmutableLocation copyOf(@Nullable ImmutableLocation location) {
        return location;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public Location asMutable() {
        return new Location(
                getWorld(),
                getX(), getY(), getZ(),
                getYaw(), getPitch()
        );
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public ImmutableLocation withWorld(@Nullable World world) {
        return new ImmutableLocation(world, getX(), getY(), getZ(), getYaw(), getPitch());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @NotNull
    @Contract(value = "-> new", pure = true)
    @Override
    public Location clone() {
        return asMutable();
    }

    @Deprecated
    @NotNull
    @Override
    public Location set(double x, double y, double z) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setX(double x) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setY(double y) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setZ(double z) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setYaw(float yaw) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setPitch(float pitch) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location setDirection(@NotNull Vector vector) {
        throw buildException();
    }

    @Deprecated
    @Override
    public void setWorld(@Nullable World world) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location add(@NotNull Vector vec) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location add(@NotNull Location vec) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location add(double x, double y, double z) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location add(@NotNull Location base, double x, double y, double z) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location subtract(@NotNull Vector vec) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location subtract(@NotNull Location vec) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location subtract(double x, double y, double z) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location subtract(@NotNull Location base, double x, double y, double z) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location multiply(double m) {
        throw buildException();
    }

    @Deprecated
    @NotNull
    @Override
    public Location zero() {
        throw buildException();
    }

    @NotNull
    @Contract("-> new")
    private UnsupportedOperationException buildException() {
        return new UnsupportedOperationException("ImmutableLocation is immutable");
    }
}

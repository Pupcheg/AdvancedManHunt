package me.supcheg.advancedmanhunt.coord;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

public class KeyedCoord implements Comparable<KeyedCoord> {
    private final int x;
    private final int z;

    private KeyedCoord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord ofKey(long key) {
        return new KeyedCoord(CoordUtil.getX(key), CoordUtil.getZ(key));
    }

    @NotNull
    @Contract("_ -> new")
    public static KeyedCoord of(int xz) {
        return new KeyedCoord(xz, xz);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static KeyedCoord of(int x, int z) {
        return new KeyedCoord(x, z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord asKeyedCoord(@NotNull Location location) {
        return of(location.getBlockX(), location.getBlockZ());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord mapX(@NotNull IntUnaryOperator unaryOperator) {
        return KeyedCoord.of(unaryOperator.applyAsInt(x), z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord mapZ(@NotNull IntUnaryOperator unaryOperator) {
        return KeyedCoord.of(x, unaryOperator.applyAsInt(z));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord mapXZ(@NotNull IntUnaryOperator unaryOperator) {
        return KeyedCoord.of(unaryOperator.applyAsInt(x), unaryOperator.applyAsInt(z));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord average(@NotNull KeyedCoord other) {
        return of((this.x + other.x) / 2, (this.z + other.z) / 2);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord add(@NotNull KeyedCoord other) {
        return of(this.x + other.x, this.z + other.z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public KeyedCoord subtract(@NotNull KeyedCoord other) {
        return of(this.x - other.x, this.z - other.z);
    }

    public long getKey() {
        return CoordUtil.getKey(x, z);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public Location asLocation() {
        return asLocation(null, 0);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Location asLocation(@Nullable World world) {
        return asLocation(world, 0);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Location asLocation(int y) {
        return asLocation(null, y);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public Location asLocation(@Nullable World world, int y) {
        return new Location(world, x, y, z, 0, 0);
    }

    @Override
    public int compareTo(@NotNull KeyedCoord o) {
        int xCompare = Integer.compare(x, o.x);
        if (xCompare == 0) {
            return Integer.compare(z, o.z);
        }
        return xCompare;
    }

    @NotNull
    public String toInclusiveString() {
        return "[" + x + "; " + z + "]";
    }

    @NotNull
    public String toExclusiveString() {
        return "(" + x + "; " + z + ")";
    }

    @NotNull
    @Override
    public String toString() {
        return toExclusiveString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof KeyedCoord coord && x == coord.x && z == coord.z);
    }

    @Override
    public int hashCode() {
        return x + z * 31;
    }
}

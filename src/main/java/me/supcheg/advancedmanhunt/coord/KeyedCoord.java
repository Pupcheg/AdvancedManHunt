package me.supcheg.advancedmanhunt.coord;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

public class KeyedCoord implements Comparable<KeyedCoord> {
    private final long key;
    private final int x;
    private final int z;

    private KeyedCoord(long key, int x, int z) {
        this.key = key;
        this.x = x;
        this.z = z;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord of(long key) {
        return new KeyedCoord(key, CoordUtil.getX(key), CoordUtil.getZ(key));
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static KeyedCoord of(int x, int z) {
        return new KeyedCoord(CoordUtil.getKey(x, z), x, z);
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
    public KeyedCoord map(@NotNull IntUnaryOperator unaryOperator) {
        return KeyedCoord.of(unaryOperator.applyAsInt(x), unaryOperator.applyAsInt(z));
    }

    public long getKey() {
        return key;
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
        return asLocation(null);
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
        return this == o || (o instanceof KeyedCoord coord && key == coord.key);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(key) + 1;
    }
}

package me.supcheg.advancedmanhunt.coord;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Coord implements Comparable<Coord> {
    private final int x;
    private final int z;

    @NotNull
    @Contract("_ -> new")
    public static Coord coordSameXZ(int xz) {
        return new Coord(xz, xz);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Coord coord(int x, int z) {
        return new Coord(x, z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Coord asKeyedCoord(@NotNull Location location) {
        return coord(location.getBlockX(), location.getBlockZ());
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord map(@NotNull IntUnaryOperator unaryOperator) {
        return Coord.coord(unaryOperator.applyAsInt(x), unaryOperator.applyAsInt(z));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord average(@NotNull Coord other) {
        return coord((this.x + other.x) / 2, (this.z + other.z) / 2);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord add(@NotNull Coord other) {
        return coord(this.x + other.x, this.z + other.z);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public Coord subtract(@NotNull Coord other) {
        return coord(this.x - other.x, this.z - other.z);
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
    public int compareTo(@NotNull Coord o) {
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
        return this == o || (o instanceof Coord coord && x == coord.x && z == coord.z);
    }

    @Override
    public int hashCode() {
        return x + z * 31;
    }
}

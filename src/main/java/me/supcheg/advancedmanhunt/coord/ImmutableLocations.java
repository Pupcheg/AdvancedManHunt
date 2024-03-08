package me.supcheg.advancedmanhunt.coord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImmutableLocations {
    @Language("RegExp")
    public static final String LOCATION_PATTERN = "([\\w_/\\\\-]+)" +
            "\\[" +
            "(spawn|[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+))" +
            "(, *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+), *[+\\-]?(?:\\d+\\.?\\d*|\\.\\d+))?" +
            "]";
    public static final int WORLD_NAME_GROUP_INDEX = 1;
    public static final int COORDS_GROUP_INDEX = 2;
    public static final int DIRECTION_GROUP_INDEX = 3;

    public static final Pattern COMPILED_PATTERN = Pattern.compile(LOCATION_PATTERN, Pattern.CASE_INSENSITIVE);
    public static final Pattern COMMA = Pattern.compile(", *");

    @NotNull
    public static String serializeLocation(@Nullable ImmutableLocation location) {
        if (location == null) {
            return "null";
        }

        World world = location.getWorld();
        Objects.requireNonNull(world, "world");

        String coords;

        if (location.equals(ImmutableLocation.immutableCopy(world.getSpawnLocation()))) {
            coords = "spawn";
        } else {
            coords = location.getX() + ", " + location.getY() + ", " + location.getZ();
        }

        String direction;
        if (location.getYaw() == 0 && location.getPitch() == 0) {
            direction = "";
        } else {
            direction = ", " + location.getYaw() + ", " + location.getPitch();
        }

        return world.getName() + '[' + coords + direction + ']';
    }

    @NotNull
    public static ImmutableLocation parseLocation(@NotNull String raw) {
        Objects.requireNonNull(raw, "Unable to parse null");

        Matcher matcher = COMPILED_PATTERN.matcher(raw);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(raw + " is not a valid location");
        }

        return parseLocation(
                matcher.group(WORLD_NAME_GROUP_INDEX),
                matcher.group(COORDS_GROUP_INDEX),
                matcher.group(DIRECTION_GROUP_INDEX)
        );
    }

    @NotNull
    private static ImmutableLocation parseLocation(@NotNull String worldName,
                                                   @NotNull String coords,
                                                   @Nullable String direction) {

        World world = Bukkit.getWorld(worldName);
        Objects.requireNonNull(world, worldName);

        double x;
        double y;
        double z;

        if (coords.equalsIgnoreCase("spawn")) {
            Location spawnLocation = world.getSpawnLocation();
            x = spawnLocation.x();
            y = spawnLocation.y();
            z = spawnLocation.z();
        } else {
            String[] rawCoords = COMMA.split(coords, 3);

            x = Double.parseDouble(rawCoords[0]);
            y = Double.parseDouble(rawCoords[1]);
            z = Double.parseDouble(rawCoords[2]);
        }

        float yaw = 0;
        float pitch = 0;

        if (direction != null) {
            String[] rawDirection = COMMA.split(direction, 3);

            yaw = Float.parseFloat(rawDirection[1]);
            pitch = Float.parseFloat(rawDirection[2]);
        }
        return ImmutableLocation.immutableLocation(world, x, y, z, yaw, pitch);
    }
}

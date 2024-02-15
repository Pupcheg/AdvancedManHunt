package me.supcheg.advancedmanhunt.util;

import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationParser {
    @Language("RegExp")
    public static final String LOCATION_PATTERN = "([\\w_/\\\\\\-]+)" +
            "\\[" +
            "((?:spawn)|(?:[+\\-]?(?:(?:\\d+\\.?\\d*)|(?:\\.\\d+)), *[+\\-]?(?:(?:\\d+\\.?\\d*)|(?:\\.\\d+)), *[+\\-]?(?:(?:\\d+\\.?\\d*)|(?:\\.\\d+))))" +
            "(, *[+\\-]?(?:(?:\\d+\\.?\\d*)|(?:\\.\\d+)), *[+\\-]?(?:(?:\\d+\\.?\\d*)|(?:\\.\\d+)))?" +
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

        if (ImmutableLocation.equal(location, world.getSpawnLocation())) {
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

        String worldName = matcher.group(WORLD_NAME_GROUP_INDEX);

        World world = Bukkit.getWorld(worldName);
        Objects.requireNonNull(world, worldName);

        return getLocation(world, matcher);
    }

    @NotNull
    private static ImmutableLocation getLocation(@NotNull World world, @NotNull Matcher matcher) {
        double x;
        double y;
        double z;

        String coords = matcher.group(COORDS_GROUP_INDEX);
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

        String direction = matcher.group(DIRECTION_GROUP_INDEX);
        if (direction != null) {
            String[] rawDirection = COMMA.split(direction, 3);

            yaw = Float.parseFloat(rawDirection[1]);
            pitch = Float.parseFloat(rawDirection[2]);
        }
        return new ImmutableLocation(world, x, y, z, yaw, pitch);
    }
}

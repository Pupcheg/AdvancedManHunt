package me.supcheg.advancedmanhunt.util;

import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

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
    public static ImmutableLocation parseImmutableLocation(@NotNull String raw) {
        return ImmutableLocation.copyOf(parseLocation(raw));
    }

    @NotNull
    public static Location parseLocation(@NotNull String raw) {
        Objects.requireNonNull(raw, "Unable to parse null");

        Matcher matcher = COMPILED_PATTERN.matcher(raw);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(raw + " is not a valid location");
        }

        String worldName = matcher.group(WORLD_NAME_GROUP_INDEX);

        World world = Bukkit.getWorld(worldName);
        Objects.requireNonNull(world, worldName);

        String coords = matcher.group(COORDS_GROUP_INDEX);

        return getLocation(coords, world, matcher);
    }

    @NotNull
    private static Location getLocation(@NotNull String coords, @NotNull World world, @NotNull Matcher matcher) {
        Location location;
        if (coords.equalsIgnoreCase("spawn")) {
            location = world.getSpawnLocation();
        } else {
            String[] rawCoords = COMMA.split(coords, 3);

            location = new Location(
                    world,
                    Double.parseDouble(rawCoords[0]),
                    Double.parseDouble(rawCoords[1]),
                    Double.parseDouble(rawCoords[2])
            );
        }

        String direction = matcher.group(DIRECTION_GROUP_INDEX);
        if (direction != null) {
            String[] rawDirection = COMMA.split(direction, 3);

            location.setYaw(Float.parseFloat(rawDirection[1]));
            location.setPitch(Float.parseFloat(rawDirection[2]));
        }
        return location;
    }
}

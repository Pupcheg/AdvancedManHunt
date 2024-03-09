package me.supcheg.advancedmanhunt.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum RealEnvironment {
    OVERWORLD("", Environment.NORMAL),
    NETHER("_nether", Environment.NETHER),
    THE_END("_the_end", Environment.THE_END);

    @NotNull
    public static RealEnvironment fromBukkit(@NotNull Environment environment) {
        return switch (environment) {
            case NORMAL -> OVERWORLD;
            case NETHER -> NETHER;
            case THE_END -> THE_END;
            default -> throw new IllegalArgumentException("Unsupported environment type: " + environment);
        };
    }

    @NotNull
    public static RealEnvironment fromWorldName(@NotNull String worldName) {
        Objects.requireNonNull(worldName, "worldName");

        RealEnvironment[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            RealEnvironment environment = values[i];
            if (worldName.endsWith(environment.getPostfix())) {
                return environment;
            }
        }

        throw new IllegalStateException("unreachable");
    }

    @NotNull
    public static RealEnvironment fromWorld(@NotNull World world) {
        return fromBukkit(world.getEnvironment());
    }

    private final String postfix;
    private final Environment asBukkit;
}

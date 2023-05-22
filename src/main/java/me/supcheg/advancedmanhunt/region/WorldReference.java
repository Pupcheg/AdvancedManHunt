package me.supcheg.advancedmanhunt.region;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.Objects;

@ToString
public class WorldReference extends WeakReference<World> {

    private final String worldName;
    private final World.Environment environment;

    protected WorldReference(@NotNull World world) {
        super(world);
        this.worldName = world.getName();
        this.environment = world.getEnvironment();
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static WorldReference of(@NotNull World world) {
        Objects.requireNonNull(world, "world");
        return new WorldReference(world);
    }

    @NotNull
    @Contract("_ -> new")
    public static WorldReference of(@NotNull String worldName) {
        return new WorldReference(Objects.requireNonNull(Bukkit.getWorld(worldName), "World not loaded: " + worldName));
    }

    @NotNull
    public String getName() {
        return worldName;
    }

    @NotNull
    public World.Environment getEnvironment() {
        return environment;
    }

    @NotNull
    public Path getDataFolder() {
        Path folder = getFolder();
        return switch (environment) {
            case NETHER -> folder.resolve("DIM-1");
            case THE_END -> folder.resolve("DIM1");
            default -> folder;
        };
    }

    @NotNull
    public Path getFolder() {
        return Bukkit.getWorldContainer().toPath().resolve(worldName);
    }

    @CanIgnoreReturnValue
    @NotNull
    public World getWorld() {
        World world = get();
        if (world == null || Bukkit.getWorld(world.getUID()) == null) {
            throw new IllegalStateException("World is unloaded!");
        }
        return world;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof WorldReference that && worldName.equals(that.worldName));
    }

    @Override
    public int hashCode() {
        return worldName.hashCode() + 1;
    }
}

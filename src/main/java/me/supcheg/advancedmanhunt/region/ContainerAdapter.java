package me.supcheg.advancedmanhunt.region;

import com.google.errorprone.annotations.MustBeClosed;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

public interface ContainerAdapter {
    @NotNull
    @Unmodifiable
    List<String> getAllWorldNames();

    @Nullable
    default String readWorldString(@NotNull World world, @NotNull String fileName) {
        byte[] data = readWorldFile(world, fileName);
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }

    default void writeWorldString(@NotNull World world, @NotNull String fileName, @NotNull String data) {
        writeWorldFile(world, fileName, data.getBytes(StandardCharsets.UTF_8));
    }

    byte @Nullable [] readWorldFile(@NotNull World world, @NotNull String fileName);

    void writeWorldFile(@NotNull World world, @NotNull String fileName, byte @NotNull [] data);

    @NotNull
    Path unpackResource(@NotNull String resourceName);

    @NotNull
    @MustBeClosed
    BufferedReader readResource(@NotNull String resourceName);

    @NotNull
    Path resolveData(@NotNull String resourceName);
}

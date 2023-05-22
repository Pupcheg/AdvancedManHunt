package me.supcheg.advancedmanhunt.region;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.charset.StandardCharsets;
import java.util.List;

public interface ContainerAdapter {
    @NotNull
    @Unmodifiable
    List<String> getAllWorldNames();

    @Nullable
    default String readString(@NotNull World world, @NotNull String fileName) {
        byte[] data = read(world, fileName);
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }

    default void writeString(@NotNull World world, @NotNull String fileName, @NotNull String data) {
        write(world, fileName, data.getBytes(StandardCharsets.UTF_8));
    }

    byte @Nullable [] read(@NotNull World world, @NotNull String fileName);

    void write(@NotNull World world, @NotNull String fileName, byte @NotNull [] data);
}

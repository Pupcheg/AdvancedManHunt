package me.supcheg.advancedmanhunt.region.impl;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DefaultContainerAdapter implements ContainerAdapter {

    @SneakyThrows
    @Override
    @NotNull
    @Unmodifiable
    public List<String> getAllWorldNames() {
        try (Stream<Path> stream = Files.list(Bukkit.getWorldContainer().toPath())) {
            return stream.map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        }
    }

    @SneakyThrows
    @Override
    public byte @Nullable [] read(@NotNull World world, @NotNull String fileName) {
        try {
            return Files.readAllBytes(resolve(world, fileName));
        } catch (FileNotFoundException | NoSuchFileException ex) {
            return null;
        }
    }

    @SneakyThrows
    @Override
    public void write(@NotNull World world, @NotNull String fileName, byte @NotNull [] data) {
        Files.write(resolve(world, fileName), data);
    }

    @NotNull
    private Path resolve(@NotNull World world, @NotNull String fileName) {
        return world.getWorldFolder().toPath().resolve(fileName);
    }
}

package me.supcheg.advancedmanhunt.paper;

import com.google.errorprone.annotations.MustBeClosed;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class PaperContainerAdapter implements ContainerAdapter, Closeable {

    private final FileSystem sourceFileSystem;
    private final Path dataDirectory;

    @SneakyThrows
    public PaperContainerAdapter(@NotNull Path pluginSource, @NotNull Path dataDirectory) {
        this.sourceFileSystem = FileSystems.newFileSystem(pluginSource);
        this.dataDirectory = dataDirectory;
    }

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
    public byte @Nullable [] readWorldFile(@NotNull World world, @NotNull String fileName) {
        try {
            return Files.readAllBytes(resolveWorldFile(world, fileName));
        } catch (FileNotFoundException | NoSuchFileException ex) {
            return null;
        }
    }

    @SneakyThrows
    @Override
    public void writeWorldFile(@NotNull World world, @NotNull String fileName, byte @NotNull [] data) {
        Files.write(resolveWorldFile(world, fileName), data);
    }

    @NotNull
    private Path resolveWorldFile(@NotNull World world, @NotNull String fileName) {
        return world.getWorldFolder().toPath().resolve(fileName);
    }

    @SneakyThrows
    @NotNull
    @Override
    public Path unpackResource(@NotNull String resourceName) {
        Path targetPath = dataDirectory.resolve(resourceName);
        if (Files.exists(targetPath)) {
            return targetPath;
        }
        Files.createDirectories(targetPath.getParent());
        Files.copy(sourceFileSystem.getPath(resourceName), targetPath);
        return targetPath;
    }

    @SneakyThrows
    @NotNull
    @MustBeClosed
    @Override
    public BufferedReader readResource(@NotNull String resourceName) {
        return Files.newBufferedReader(sourceFileSystem.getPath(resourceName));
    }

    @SneakyThrows
    @NotNull
    @MustBeClosed
    @Override
    public Stream<Path> readResourcesTree(@NotNull String directory) {
        return Files.walk(sourceFileSystem.getPath(directory));
    }

    @NotNull
    @Override
    public Path resolveData(@NotNull String resourceName) {
        return dataDirectory.resolve(resourceName);
    }

    @Override
    public void close() throws IOException {
        sourceFileSystem.close();
    }
}

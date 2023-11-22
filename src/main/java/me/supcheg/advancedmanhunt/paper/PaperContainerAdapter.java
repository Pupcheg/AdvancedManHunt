package me.supcheg.advancedmanhunt.paper;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

    @NotNull
    @Override
    public Path resolveResource(@NotNull String resourceName) {
        return sourceFileSystem.getPath(resourceName);
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

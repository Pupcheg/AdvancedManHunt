package me.supcheg.advancedmanhunt.io;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultContainerAdapter implements ContainerAdapter, Closeable {

    private final FileSystem sourceFileSystem;
    private final Path dataDirectory;

    @SneakyThrows
    public DefaultContainerAdapter(@NotNull Path pluginSource, @NotNull Path dataDirectory) {
        this.sourceFileSystem = FileSystems.newFileSystem(pluginSource);
        this.dataDirectory = dataDirectory;
    }

    @NotNull
    @Override
    public Path unpackResource(@NotNull String resourceName) {
        return findResource(resourceName, true);
    }

    @NotNull
    @Override
    public Path resolveResource(@NotNull String resourceName) {
        return findResource(resourceName, false);
    }

    @SneakyThrows
    private Path findResource(@NotNull String name, boolean unpack) {
        Path unpacked = dataDirectory.resolve(name);
        if (Files.exists(unpacked)) {
            return unpacked;
        }

        Path packed = sourceFileSystem.getPath(name);
        if (Files.notExists(packed)) {
            throw new IllegalArgumentException("No such resource: " + name);
        }

        if (unpack) {
            Files.createDirectories(unpacked.getParent());
            Files.copy(packed, unpacked);
            return unpacked;
        }

        return packed;
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

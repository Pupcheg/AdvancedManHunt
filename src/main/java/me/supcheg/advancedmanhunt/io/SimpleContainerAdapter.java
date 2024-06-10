package me.supcheg.advancedmanhunt.io;

import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.platform.paper.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SimpleContainerAdapter implements ContainerAdapter {

    private final FileSystem sourceFileSystem;
    private final Path dataDirectory;

    @Inject
    @SneakyThrows
    public SimpleContainerAdapter() {
        JavaPlugin plugin = PluginUtil.getPlugin();
        this.sourceFileSystem = FileSystems.newFileSystem(PluginUtil.getFile(plugin).toPath());
        this.dataDirectory = plugin.getDataFolder().toPath();
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

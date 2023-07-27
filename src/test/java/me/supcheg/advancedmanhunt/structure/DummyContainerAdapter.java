package me.supcheg.advancedmanhunt.structure;

import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DummyContainerAdapter implements ContainerAdapter {
    @Override
    @NotNull
    @Unmodifiable
    public List<String> getAllWorldNames() {
        return Collections.emptyList();
    }

    @Override
    public byte @Nullable [] readWorldFile(@NotNull World world, @NotNull String fileName) {
        return null;
    }

    @Override
    public void writeWorldFile(@NotNull World world, @NotNull String fileName, byte @NotNull [] data) {
    }

    @Override
    @NotNull
    public Path unpackResource(@NotNull String resourceName) {
        throw new UnsupportedOperationException("#unpackResource(String) is not supported");
    }

    @Override
    @NotNull
    public BufferedReader readResource(@NotNull String resourceName) {
        throw new UnsupportedOperationException("#readResource(String) is not supported");
    }

    @Override
    @NotNull
    public Stream<Path> readResourcesTree(@NotNull String directory) {
        throw new UnsupportedOperationException("#readResourcesTree(String) is not supported");
    }

    @Override
    @NotNull
    public Path resolveData(@NotNull String resourceName) {
        throw new UnsupportedOperationException("#resolveData(String) is not supported");
    }
}

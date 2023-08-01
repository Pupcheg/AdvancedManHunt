package me.supcheg.advancedmanhunt.util;

import com.google.errorprone.annotations.MustBeClosed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface ContainerAdapter {
    @NotNull
    @Unmodifiable
    List<String> getAllWorldNames();

    @NotNull
    Path unpackResource(@NotNull String resourceName);

    @NotNull
    @MustBeClosed
    BufferedReader readResource(@NotNull String resourceName);

    @NotNull
    @MustBeClosed
    Stream<Path> readResourcesTree(@NotNull String directory);

    @NotNull
    Path resolveData(@NotNull String resourceName);
}

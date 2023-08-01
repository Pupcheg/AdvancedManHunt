package me.supcheg.advancedmanhunt.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Path;
import java.util.List;

public interface ContainerAdapter {
    @NotNull
    @Unmodifiable
    List<String> getAllWorldNames();

    @NotNull
    Path unpackResource(@NotNull String resourceName);

    @NotNull
    Path resolveResource(@NotNull String resourceName);

    @NotNull
    Path resolveData(@NotNull String resourceName);
}

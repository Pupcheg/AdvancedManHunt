package me.supcheg.advancedmanhunt.io;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface ContainerAdapter {
    @NotNull
    Path unpackResource(@NotNull String resourceName);

    @NotNull
    Path resolveResource(@NotNull String resourceName);

    @NotNull
    Path resolveData(@NotNull String resourceName);
}

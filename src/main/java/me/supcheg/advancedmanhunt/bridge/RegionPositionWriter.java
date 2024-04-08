package me.supcheg.advancedmanhunt.bridge;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface RegionPositionWriter {
    void writePositionsToRegion(@NotNull Path regionPath);
}

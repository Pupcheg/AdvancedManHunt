package me.supcheg.advancedmanhunt.util;

import com.google.common.io.MoreFiles;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Regions {
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static KeyedCoord getRegionCoords(@NotNull Path regionFile) {
        String fileName = MoreFiles.getNameWithoutExtension(regionFile);
        int lastDotIndex = fileName.lastIndexOf('.');

        int currentRegionX;
        int currentRegionZ;
        try {
            currentRegionX = Integer.parseInt(fileName.substring(2, lastDotIndex));
            currentRegionZ = Integer.parseInt(fileName.substring(lastDotIndex + 1));
        } catch (StringIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Invalid file name: " + fileName + " in " + regionFile, ex);
        }
        return KeyedCoord.of(currentRegionX, currentRegionZ);
    }
}

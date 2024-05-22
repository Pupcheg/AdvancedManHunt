package me.supcheg.advancedmanhunt.region;

import com.google.common.io.MoreFiles;
import io.papermc.paper.math.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Regions {
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Position getRegionCoords(@NotNull Path regionFile) {
        String fileName = MoreFiles.getNameWithoutExtension(regionFile);
        int lastDotIndex = fileName.lastIndexOf('.');

        int x;
        int z;
        try {
            x = Integer.parseInt(fileName.substring(2, lastDotIndex));
            z = Integer.parseInt(fileName.substring(lastDotIndex + 1));
        } catch (StringIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Invalid file name: " + fileName + " in " + regionFile, ex);
        }
        return Position.block(x, 0, z);
    }
}

package me.supcheg.advancedmanhunt.structure;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.impl.AbstractTemplateLoader;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;

@RequiredArgsConstructor
public class PointingTemplateLoader extends AbstractTemplateLoader {
    private final Path imageOutFolder;

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        checkRegionState(region, template);
        region.setBusy(true);

        BufferedImage image = new BufferedImage(
                MAX_REGION_SIDE_SIZE.getRegions(),
                MAX_REGION_SIDE_SIZE.getRegions(),
                BufferedImage.TYPE_INT_RGB
        );

        Distance templateSideSize = template.getSideSize();
        KeyedCoord delta = countOffsetInRegions(templateSideSize);

        for (int x = 0; x < templateSideSize.getRegions(); x++) {
            for (int z = 0; z < templateSideSize.getRegions(); z++) {
                image.setRGB(delta.getX() + x, delta.getZ() + z, randomColor());
            }
        }

        Path imageOut = imageOutFolder.resolve(region.getWorld().getName() + ".png");
        Files.createDirectories(imageOutFolder);
        try (OutputStream out = Files.newOutputStream(imageOut, StandardOpenOption.CREATE)) {
            ImageIO.write(image, "png", out);
        }

        region.setBusy(false);
        return CompletableFuture.completedFuture(null);
    }

    private static int randomColor() {
        return ThreadLocalRandom.current().nextInt(0xAAAAAA, 0xFFFFFF);
    }
}

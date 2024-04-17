package me.supcheg.advancedmanhunt.structure;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.impl.AsyncTemplateLoader;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

@RequiredArgsConstructor
public class PointingTemplateLoader extends AsyncTemplateLoader {
    private final Path imageOutFolder;
    private BufferedImage image;

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        image = new BufferedImage(
                MAX_REGION_RADIUS.getRegions() * 2,
                MAX_REGION_RADIUS.getRegions() * 2,
                BufferedImage.TYPE_INT_RGB
        );

        CompletableFuture<Void> result = super.loadTemplate(region, template);

        Path imageOut = imageOutFolder.resolve(region.getWorld().getName() + ".png");
        Files.createDirectories(imageOutFolder);
        try (OutputStream out = Files.newOutputStream(imageOut, StandardOpenOption.CREATE)) {
            ImageIO.write(image, "png", out);
        }

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(imageOut.toUri());
        }

        return result;
    }

    @NotNull
    @Override
    protected ActionRunnable createRunnable(@NotNull RegionLoadContext ctx) {
        return () -> {
            Coord coord = ctx.getTargetCoord();
            image.setRGB(coord.getX(), coord.getZ(), randomColor());
        };
    }

    private static int randomColor() {
        return ThreadSafeRandom.randomInt(0xAAAAAA, 0xFFFFFF);
    }
}

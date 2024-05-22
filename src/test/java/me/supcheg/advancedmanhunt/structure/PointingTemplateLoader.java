package me.supcheg.advancedmanhunt.structure;

import io.papermc.paper.math.Position;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.random.ThreadSafeRandom;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.impl.AsyncTemplateLoader;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;

@RequiredArgsConstructor
public class PointingTemplateLoader extends AsyncTemplateLoader {
    private final Path imageOutFolder;
    private BufferedImage image;

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        image = new BufferedImage(
                MAX_REGION_SIDE_SIZE.getRegions(),
                MAX_REGION_SIDE_SIZE.getRegions(),
                BufferedImage.TYPE_INT_RGB
        );

        CompletableFuture<Void> result = super.loadTemplate(region, template);

        Path imageOut = imageOutFolder.resolve(region.positionSource().world().getWorld().getName() + ".png");
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
            Position coord = ctx.getTargetPos();
            image.setRGB(coord.blockX(), coord.blockZ(), randomColor());
        };
    }

    private static int randomColor() {
        return ThreadSafeRandom.randomInt(0xAAAAAA, 0xFFFFFF);
    }
}

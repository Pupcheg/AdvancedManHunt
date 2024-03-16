package me.supcheg.advancedmanhunt.template.impl;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.concurrent.CompletableFutures;
import me.supcheg.advancedmanhunt.coord.Coord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.Regions;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;

@CustomLog
public class ReplacingTemplateLoader extends AbstractTemplateLoader {
    private final ExecutorService executor;

    public ReplacingTemplateLoader() {
        this.executor = Executors.newFixedThreadPool(config().templateLoad.threadPoolSize);
    }

    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        checkRegionState(region, template);
        region.setBusy(true);

        Set<Path> templateData = template.getData();

        if (templateData.isEmpty()) {
            if (config().templateLoad.emptyTemplateWarning) {
                log.warn("The template directory ({}) does not contain" +
                        " any files. This may be an error. You can disable this" +
                        " notification in the configuration (template_load.empty_warning)", template);
            }
            region.setBusy(false);
            return CompletableFuture.completedFuture(null);
        }

        Path worldFolder = region.getWorldReference().getDataFolder();
        Coord offset = countOffsetInRegions(template.getRadius());

        return templateData.stream()
                .map(path -> new RegionReplaceRunnable(path, worldFolder, offset))
                .map(runnable -> CompletableFuture.runAsync(runnable, executor))
                .collect(CompletableFutures.joinFutures())
                .thenRun(() -> region.setBusy(false));
    }

    @NotNull
    private static Path getNameWithOffset(@NotNull Coord offset, @NotNull Coord coord) {
        int realRegionX = offset.getX() + coord.getX();
        int realRegionZ = offset.getZ() + coord.getZ();

        String destinationFileName = "r." + realRegionX + "." + realRegionZ + ".mca";
        return Path.of(destinationFileName);
    }

    private static class RegionReplaceRunnable implements Runnable {
        private final Path source;
        private final Path target;

        public RegionReplaceRunnable(@NotNull Path source, @NotNull Path worldFolder, @NotNull Coord offset) {
            this.source = source;
            Coord sourceCoords = Regions.getRegionCoords(source);
            this.target = worldFolder.resolve(source.getParent().getFileName())
                    .resolve(getNameWithOffset(offset, sourceCoords));
        }

        @Override
        public void run() {
            try {
                Files.createDirectories(target.getParent());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                log.error("An error occurred while copying file '{}' to '{}'", source, target, e);
            }
        }

    }

}

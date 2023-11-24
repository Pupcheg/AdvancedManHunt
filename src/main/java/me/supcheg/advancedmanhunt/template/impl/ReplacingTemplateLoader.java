package me.supcheg.advancedmanhunt.template.impl;

import lombok.CustomLog;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.util.Regions;
import me.supcheg.advancedmanhunt.util.concurrent.CompletableFutures;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CustomLog
public class ReplacingTemplateLoader extends AbstractTemplateLoader {
    private final ExecutorService executor;

    public ReplacingTemplateLoader() {
        this.executor = Executors.newFixedThreadPool(AdvancedManHuntConfig.TemplateLoad.THREAD_POOL_SIZE);
    }

    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        checkRegionState(region, template);
        region.setBusy(true);

        Set<Path> templateData = template.getData();

        if (templateData.isEmpty()) {
            if (AdvancedManHuntConfig.TemplateLoad.EMPTY_TEMPLATE_WARNING) {
                log.warn("The template directory ({}) does not contain" +
                        " any files. This may be an error. You can disable this" +
                        " notification in the configuration (template_load.empty_warning)", template);
            }
            region.setBusy(false);
            return CompletableFuture.completedFuture(null);
        }

        Path worldFolder = region.getWorldReference().getDataFolder();
        KeyedCoord offset = countOffsetInRegions(template.getRadius());

        return CompletableFutures.allOf(templateData,
                        regionPath -> new RegionReplaceRunnable(regionPath, worldFolder, offset), executor)
                .thenRun(() -> region.setBusy(false));
    }

    @NotNull
    private static Path getNameWithOffset(@NotNull KeyedCoord offset, @NotNull KeyedCoord coord) {
        int realRegionX = offset.getX() + coord.getX();
        int realRegionZ = offset.getZ() + coord.getZ();

        String destinationFileName = "r." + realRegionX + "." + realRegionZ + ".mca";
        return Path.of(destinationFileName);
    }

    private static class RegionReplaceRunnable implements Runnable {
        private final Path source;
        private final Path target;

        public RegionReplaceRunnable(@NotNull Path source, @NotNull Path worldFolder, @NotNull KeyedCoord offset) {
            this.source = source;
            KeyedCoord sourceCoords = Regions.getRegionCoords(source);
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

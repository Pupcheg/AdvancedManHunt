package me.supcheg.advancedmanhunt.template.impl;

import com.google.common.io.MoreFiles;
import lombok.CustomLog;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.util.CompletableFutures;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CustomLog
@SuppressWarnings("UnstableApiUsage")
public class ReplacingTemplateLoader extends AbstractTemplateLoader {
    private final ExecutorService executor;

    public ReplacingTemplateLoader() {
        this.executor = Executors.newFixedThreadPool(AdvancedManHuntConfig.TemplateLoad.THREAD_POOL_SIZE);
    }

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        checkRegionState(region, template);
        prepareRegion(region);

        Set<Path> templateData = template.getData();

        if (templateData.isEmpty()) {
            if (AdvancedManHuntConfig.TemplateLoad.EMPTY_TEMPLATE_WARNING) {
                log.warn("The template directory ({}) does" +
                        " not contain any files. This may be an error. You can disable this" +
                        " notification in the configuration (template_load.empty_warning)", template);
            }
            region.setBusy(false);
            return CompletableFuture.completedFuture(null);
        }
        Path worldFolder = region.getWorldReference().getDataFolder();
        KeyedCoord delta = countDeltaInRegions(template.getSideSize());

        return CompletableFutures.allOf(templateData, regionPath -> copyRegion(regionPath, worldFolder, delta))
                .thenRun(() -> region.setBusy(false));
    }

    @SneakyThrows
    @NotNull
    private CompletableFuture<?> copyRegion(@NotNull Path regionPath, @NotNull Path destinationWorldFolder,
                                            @NotNull KeyedCoord delta) {
        String fileExtension = MoreFiles.getFileExtension(regionPath);

        Path destionationPath = destinationWorldFolder
                .resolve(regionPath.getParent().getFileName())
                .resolve(getNameWithDelta(regionPath, delta, fileExtension));
        return CompletableFuture.runAsync(() -> tryCopyFile(regionPath, destionationPath, fileExtension), executor);
    }

    @NotNull
    private static Path getNameWithDelta(@NotNull Path path, @NotNull KeyedCoord delta, @NotNull String fileExtension) {
        KeyedCoord regionCoords = getRegionCoords(path);

        int realRegionX = delta.getX() + regionCoords.getX();
        int realRegionZ = delta.getZ() + regionCoords.getZ();

        String destinationFileName = "r.%d.%d.%s".formatted(realRegionX, realRegionZ, fileExtension);
        return Path.of(destinationFileName);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static KeyedCoord getRegionCoords(@NotNull Path regionFile) {
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

    private void tryCopyFile(@NotNull Path source, @NotNull Path target, @NotNull String extension) {
        try {
            Files.createDirectories(target.getParent());
            if (extension.equals("mca")) {
                KeyedCoord regionCoords = getRegionCoords(target);
                try (ChunkCoordReplacer replacer = new ChunkCoordReplacer(source, target)) {
                    replacer.copyRegion(regionCoords.getX(), regionCoords.getZ());
                }
            } else {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            log.error("An error occurred while copying file '{}' to '{}'", source, target, e);
        }
    }

}

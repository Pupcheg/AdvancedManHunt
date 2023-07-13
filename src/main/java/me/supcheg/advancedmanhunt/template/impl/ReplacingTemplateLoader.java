package me.supcheg.advancedmanhunt.template.impl;

import com.google.common.io.MoreFiles;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.concurrent.CompletableFutures;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.exception.TemplateLoadException;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("UnstableApiUsage")
public class ReplacingTemplateLoader implements TemplateLoader {
    private static final CustomLogger LOGGER = CustomLogger.getLogger(ReplacingTemplateLoader.class);

    private final ExecutorService executor;

    public ReplacingTemplateLoader() {
        this.executor = Executors.newFixedThreadPool(AdvancedManHuntConfig.TemplateLoad.THREAD_POOL_SIZE);
    }

    @SneakyThrows
    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        assertCanPut(region, template);

        if (region.isBusy()) {
            throw buildException("Region is busy!", region);
        }
        region.setBusy(true);

        boolean unloadResult = region.unload();
        if (!unloadResult) {
            throw buildException("Region can't be unloaded!", region);
        }

        Set<Path> templateData = template.getData();

        if (templateData.isEmpty()) {
            if (AdvancedManHuntConfig.TemplateLoad.EMPTY_TEMPLATE_WARNING) {
                LOGGER.warn("The template directory ({}) does" +
                        " not contain any files. This may be an error. You can disable this" +
                        " notification in the configuration (template_load.empty_warning)", template);
            }
            region.setBusy(false);
            return CompletableFuture.completedFuture(null);
        }
        Path worldFolder = region.getWorldReference().getDataFolder();

        int startRegionX = region.getStartRegion().getX();
        int startRegionZ = region.getStartRegion().getZ();

        return CompletableFutures.allOf(
                templateData,
                regionPath -> {
                    Path destionationPath = worldFolder
                            .resolve(regionPath.getParent().getFileName()) // entities/poi/regions
                            .resolve(getNameWithDelta(regionPath, startRegionX, startRegionZ)); // r.12.-10.mca

                    return CompletableFuture.runAsync(() -> tryCopyFile(regionPath, destionationPath), executor);
                }
        );
    }

    @NotNull
    private static Path getNameWithDelta(@NotNull Path path, int deltaX, int deltaZ) {
        String fileName = MoreFiles.getNameWithoutExtension(path);
        String extension = MoreFiles.getFileExtension(path);
        int lastDotIndex = fileName.lastIndexOf('.');

        int currentRegionX;
        int currentRegionZ;
        try {
            currentRegionX = Integer.parseInt(fileName.substring(2, lastDotIndex));
            currentRegionZ = Integer.parseInt(fileName.substring(lastDotIndex + 1));
        } catch (StringIndexOutOfBoundsException ex) {
            throw new IllegalArgumentException("Invalid file name: " + fileName + " in " + path, ex);
        }

        int realRegionX = deltaX + currentRegionX;
        int realRegionZ = deltaZ + currentRegionZ;

        String destinationFileName = "r.%d.%d.%s".formatted(realRegionX, realRegionZ, extension);
        return Path.of(destinationFileName);
    }

    private void tryCopyFile(@NotNull Path source, @NotNull Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LOGGER.error("An error occurred while copying file '{}' to '{}'", source, target, e);
        }
    }

    private static void assertCanPut(@NotNull GameRegion region, @NotNull Template template) {
        if (region.getSideSize().isLessThan(template.getSideSize())) {
            throw buildException(region.getSideSize() + " > " + template.getSideSize(), region);
        }
    }

    @NotNull
    @Contract("_, _ -> new")
    private static TemplateLoadException buildException(@NotNull String message, @NotNull GameRegion region) {
        return new TemplateLoadException("[" + region + "] " + message);
    }

}

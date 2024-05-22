package me.supcheg.advancedmanhunt.template.impl;

import io.papermc.paper.math.Position;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.concurrent.CompletableFutures;
import me.supcheg.advancedmanhunt.math.distance.Distance;
import me.supcheg.advancedmanhunt.math.distance.DistancePair;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.Regions;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.exception.TemplateLoadException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

@Slf4j
public abstract class AsyncTemplateLoader implements TemplateLoader {
    private final ExecutorService executor = Executors.newFixedThreadPool(config().templateLoad.threadPoolSize);

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

        Path worldFolder = region.positionSource().world().getDataFolder();
        DistancePair offset = countOffset(template.getRadius());

        return templateData.stream()
                .map(path -> {
                    Position originalCoords = Regions.getRegionCoords(path);
                    Position targetCoords = originalCoords.offset(offset.x(), 0, offset.z());
                    RegionLoadContext ctx = new RegionLoadContext(
                            path, worldFolder,
                            originalCoords, offset, targetCoords
                    );
                    return safeRunnable(ctx, createRunnable(ctx));
                })
                .map(runnable -> CompletableFuture.runAsync(runnable, executor))
                .collect(CompletableFutures.joinFutures())
                .thenRun(() -> region.setBusy(false));
    }

    @NotNull
    private Runnable safeRunnable(RegionLoadContext ctx, ActionRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable thr) {
                log.error(
                        "An error occurred while loading region {} to {}",
                        ctx.getRegionFile(), ctx.getTargetPos(), thr
                );
            }
        };
    }

    @NotNull
    protected abstract ActionRunnable createRunnable(@NotNull RegionLoadContext ctx);

    @Data
    public static final class RegionLoadContext {
        private final Path regionFile;
        private final Path worldFolder;
        private final Position originalPos;
        private final Position offset;
        private final Position targetPos;
    }

    private void checkRegionState(@NotNull GameRegion region, @NotNull Template template) {
        if (MAX_REGION_RADIUS.isLessThan(template.getRadius())) {
            throw new TemplateLoadException(MAX_REGION_RADIUS + " <" + template.getRadius());
        }

        if (region.isBusy()) {
            throw new TemplateLoadException("Region is busy");
        }
    }

    @NotNull
    @Contract("_ -> new")
    private DistancePair countOffset(@NotNull Distance templateRadius) {
        return DistancePair.ofSame(MAX_REGION_RADIUS.subtract(templateRadius));
    }
}

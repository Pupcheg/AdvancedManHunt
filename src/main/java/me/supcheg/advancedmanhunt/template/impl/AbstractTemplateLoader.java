package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.exception.TemplateLoadException;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;

public abstract class AbstractTemplateLoader implements TemplateLoader {

    protected static void checkRegionState(@NotNull GameRegion region, @NotNull Template template) {
        if (MAX_REGION_SIDE_SIZE.isLessThan(template.getSideSize())) {
            throw buildException(MAX_REGION_SIDE_SIZE + " > " + template.getSideSize(), region);
        }

        if (region.isBusy()) {
            throw buildException("Region is busy!", region);
        }
    }

    protected static void prepareRegion(@NotNull GameRegion region) {
        region.setBusy(true);

        boolean unloadResult = region.unload();
        if (!unloadResult) {
            throw buildException("Region can't be unloaded!", region);
        }
    }

    @NotNull
    @Contract("_ -> new")
    protected static KeyedCoord countOffsetInRegions(@NotNull Distance templateSideSize) {
        return KeyedCoord.of(MAX_REGION_SIDE_SIZE.subtract(templateSideSize).getRegions() / 2);
    }

    @NotNull
    @Contract("_, _ -> new")
    protected static TemplateLoadException buildException(@NotNull String message, @NotNull GameRegion region) {
        return new TemplateLoadException("[" + region + "] " + message);
    }
}

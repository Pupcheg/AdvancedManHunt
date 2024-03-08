package me.supcheg.advancedmanhunt.template.impl;

import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.exception.TemplateLoadException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;

public abstract class AbstractTemplateLoader implements TemplateLoader {

    protected void checkRegionState(@NotNull GameRegion region, @NotNull Template template) {
        if (MAX_REGION_RADIUS.isLessThan(template.getRadius())) {
            throw buildException(MAX_REGION_RADIUS + " > " + template.getRadius(), region);
        }

        if (region.isBusy()) {
            throw buildException("Region is busy!", region);
        }
    }

    @NotNull
    @Contract("_ -> new")
    protected KeyedCoord countOffsetInRegions(@NotNull Distance templateRadius) {
        return KeyedCoord.of(MAX_REGION_RADIUS.subtract(templateRadius).getRegions());
    }

    @NotNull
    @Contract("_, _ -> new")
    protected TemplateLoadException buildException(@NotNull String message, @NotNull GameRegion region) {
        return new TemplateLoadException("[" + region + "] " + message);
    }
}

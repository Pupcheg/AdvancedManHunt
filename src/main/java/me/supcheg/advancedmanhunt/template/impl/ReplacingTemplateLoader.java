package me.supcheg.advancedmanhunt.template.impl;

import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import me.supcheg.advancedmanhunt.coord.Coord;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@NoArgsConstructor(onConstructor_ = {@Inject})
public class ReplacingTemplateLoader extends AsyncTemplateLoader {
    @NotNull
    @Override
    protected ActionRunnable createRunnable(@NotNull RegionLoadContext ctx) {
        Coord targetCoord = ctx.getTargetCoord();
        Path source = ctx.getRegionFile();
        Path target = ctx.getWorldFolder()
                .resolve(source.getParent().getFileName())
                .resolve("r.%d.%d.mca".formatted(targetCoord.getX(), targetCoord.getZ()));

        return () -> {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        };
    }
}

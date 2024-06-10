package me.supcheg.advancedmanhunt.template.impl;

import io.papermc.paper.math.Position;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.action.ActionRunnable;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@NoArgsConstructor(onConstructor_ = {@Inject})
public final class ReplacingTemplateLoader extends AsyncTemplateLoader {
    @NotNull
    @Override
    protected ActionRunnable createRunnable(@NotNull RegionLoadContext ctx) {
        Position targetPos = ctx.getTargetPos();
        Path source = ctx.getRegionFile();
        Path target = ctx.getWorldFolder()
                .resolve(source.getParent().getFileName())
                .resolve("r.%d.%d.mca".formatted(targetPos.blockX(), targetPos.blockZ()));

        return () -> {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        };
    }
}

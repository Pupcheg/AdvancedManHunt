package me.supcheg.advancedmanhunt.template;

import me.supcheg.advancedmanhunt.region.GameRegion;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TemplateLoader {
    @NotNull
    CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template);
}

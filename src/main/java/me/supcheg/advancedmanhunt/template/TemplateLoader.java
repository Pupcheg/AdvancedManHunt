package me.supcheg.advancedmanhunt.template;

import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.concurrent.CompletableFutures;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface TemplateLoader {
    @NotNull
    CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template);

    @NotNull
    default CompletableFuture<Void> loadTemplates(@NotNull Map<GameRegion, Template> region2template) {
        return CompletableFutures.allOf(region2template.entrySet(), entry -> loadTemplate(entry.getKey(), entry.getValue()));
    }
}

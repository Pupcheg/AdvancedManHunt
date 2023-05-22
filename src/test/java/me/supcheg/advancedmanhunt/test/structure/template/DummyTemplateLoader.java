package me.supcheg.advancedmanhunt.test.structure.template;

import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DummyTemplateLoader implements TemplateLoader {

    public static final TemplateLoader INSTANCE = new DummyTemplateLoader();

    private DummyTemplateLoader() {
    }

    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplate(@NotNull GameRegion region, @NotNull Template template) {
        return CompletableFuture.completedFuture(null);
    }

    @NotNull
    @Override
    public CompletableFuture<Void> loadTemplates(@NotNull Map<GameRegion, Template> region2template) {
        return CompletableFuture.completedFuture(null);
    }
}

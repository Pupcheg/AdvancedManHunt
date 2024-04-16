package me.supcheg.advancedmanhunt.gui.impl.common.texture;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MapTextureWrapper implements TextureWrapper {
    private final Map<String, PaperItemTexture> paperItems;
    private final Map<String, ComponentGuiTexture> guiComponents;

    public MapTextureWrapper() {
        this.paperItems = new HashMap<>();
        this.guiComponents = new HashMap<>();
    }

    @NotNull
    @Override
    public ComponentGuiTexture getGuiTexture(@NotNull String resourcePath) {
        return Objects.requireNonNull(
                guiComponents.get(resourcePath),
                () -> "ComponentGuiTexture with path='%s' not found".formatted(resourcePath)
        );
    }

    @NotNull
    @Override
    public PaperItemTexture getPaperTexture(@NotNull String resourcePath) {
        return Objects.requireNonNull(
                paperItems.get(resourcePath),
                () -> "PaperItemTexture with path='%s' not found".formatted(resourcePath)
        );
    }

    public void addPaperItemTexture(@NotNull PaperItemTexture texture) {
        paperItems.put(texture.getPath(), texture);
        log.debug("Added: {}", texture);
    }

    public void removePaperItemTexture(@NotNull String resourcePath) {
        paperItems.remove(resourcePath);
        log.debug("Removed: {}", resourcePath);
    }

    public void addComponentGuiTexture(@NotNull ComponentGuiTexture texture) {
        guiComponents.put(texture.getPath(), texture);
        log.debug("Added: {}", texture);
    }

    public void removeComponentGuiTexture(@NotNull String resourcePath) {
        guiComponents.remove(resourcePath);
        log.debug("Removed: {}", resourcePath);
    }
}

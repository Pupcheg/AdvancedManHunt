package me.supcheg.advancedmanhunt.gui.impl.inventory.texture;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MapTextureWrapper implements TextureWrapper {
    private final Object2IntMap<String> resourcePath2customModelData;
    private final Map<String, Component> resourcePath2component;

    public MapTextureWrapper() {
        this.resourcePath2customModelData = new Object2IntOpenHashMap<>();
        this.resourcePath2component = new HashMap<>();
    }

    @Override
    public int getPaperCustomModelData(@NotNull String resourcePath) {
        return resourcePath2customModelData.getOrDefault(resourcePath, 0);
    }

    @NotNull
    @Override
    public Component getGuiBackgroundComponent(@NotNull String resourcePath) {
        return resourcePath2component.getOrDefault(resourcePath, Component.empty());
    }

    public void putButton(@NotNull String resourcePath, int customModelData) {
        resourcePath2customModelData.put(resourcePath, customModelData);
    }

    public void putGui(@NotNull String resourcePath, @NotNull Component component) {
        resourcePath2component.put(resourcePath, component);
    }
}

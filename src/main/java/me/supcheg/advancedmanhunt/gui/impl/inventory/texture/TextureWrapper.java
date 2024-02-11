package me.supcheg.advancedmanhunt.gui.impl.inventory.texture;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface TextureWrapper {
    int getPaperCustomModelData(@NotNull String resourcePath);

    @NotNull
    Component getGuiBackgroundComponent(@NotNull String resourcePath);
}

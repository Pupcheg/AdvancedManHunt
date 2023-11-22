package me.supcheg.advancedmanhunt.gui.api.render;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface TextureWrapper {
    int getPaperCustomModelData(@NotNull String resourcePath);

    @NotNull
    Component getGuiBackgroundComponent(@NotNull String resourcePath);
}

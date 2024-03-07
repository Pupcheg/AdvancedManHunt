package me.supcheg.advancedmanhunt.gui.impl.common.texture;

import org.jetbrains.annotations.NotNull;

public interface TextureWrapper {
    @NotNull
    PaperItemTexture getPaperTexture(@NotNull String resourcePath);

    @NotNull
    ComponentGuiTexture getGuiTexture(@NotNull String resourcePath);
}

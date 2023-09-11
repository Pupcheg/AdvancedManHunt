package me.supcheg.advancedmanhunt.gui.api.render;

import net.kyori.adventure.text.Component;

public interface TextureWrapper {
    int getPaperCustomModelData(String resourcePath);

    Component getGuiBackgroundComponent(String resourcePath);
}

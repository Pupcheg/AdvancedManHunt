package me.supcheg.advancedmanhunt.gui.impl.common.texture;

import lombok.Data;
import net.kyori.adventure.text.Component;

@Data
public class ComponentGuiTexture {
    private final String path;
    private final Component component;
    private final int height;
    private final int width;
}

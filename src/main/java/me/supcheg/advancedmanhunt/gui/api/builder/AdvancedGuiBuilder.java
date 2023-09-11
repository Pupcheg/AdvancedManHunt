package me.supcheg.advancedmanhunt.gui.api.builder;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;

public interface AdvancedGuiBuilder {
    AdvancedGuiBuilder rows(int rows);

    AdvancedGuiBuilder individual();

    AdvancedGuiBuilder button(AdvancedButtonBuilder button);

    AdvancedGuiBuilder background(String pngSubPath);

    AdvancedGuiBuilder animatedBackground(String pngSubPathTemplate, int size, Duration period);

    AdvancedGuiBuilder lazyBackground(GuiBackgroundFunction function);

    AdvancedGuiBuilder lazyAnimatedBackground(GuiBackgroundFunction function, Duration period);

    AdvancedGui buildAndRegister();
}

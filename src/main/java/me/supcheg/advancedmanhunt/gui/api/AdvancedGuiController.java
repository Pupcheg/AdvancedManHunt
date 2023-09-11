package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;

public interface AdvancedGuiController {
    AdvancedGuiBuilder gui();

    AdvancedButtonBuilder button();

    AdvancedGui buildAndRegister(AdvancedGuiBuilder builder);

    void unregister(AdvancedGui gui);
}

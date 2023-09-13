package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface AdvancedGuiController {
    @NotNull
    @Contract("-> new")
    AdvancedGuiBuilder gui();

    @NotNull
    @Contract("-> new")
    AdvancedButtonBuilder button();

    @NotNull
    @Contract("_ -> new")
    AdvancedGui buildAndRegister(@NotNull AdvancedGuiBuilder builder);

    void unregister(@NotNull AdvancedGui gui);
}

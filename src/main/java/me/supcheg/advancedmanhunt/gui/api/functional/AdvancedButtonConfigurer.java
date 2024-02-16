package me.supcheg.advancedmanhunt.gui.api.functional;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedButtonBuilder;
import org.jetbrains.annotations.NotNull;

public interface AdvancedButtonConfigurer {
    void configure(@NotNull AdvancedButtonBuilder builder);
}

package me.supcheg.advancedmanhunt.gui.api;

import me.supcheg.advancedmanhunt.gui.api.builder.AdvancedGuiBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface AdvancedGuiLoader {
    @NotNull
    AdvancedGuiBuilder loadResource(@NotNull String path) throws IOException;

    void saveResource(@NotNull AdvancedGuiBuilder gui, @NotNull String path) throws IOException;
}

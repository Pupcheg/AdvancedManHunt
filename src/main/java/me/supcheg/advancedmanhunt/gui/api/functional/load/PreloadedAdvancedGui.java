package me.supcheg.advancedmanhunt.gui.api.functional.load;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.jetbrains.annotations.NotNull;

public interface PreloadedAdvancedGui {
    @NotNull
    AdvancedGui with(@NotNull Object obj);
}

package me.supcheg.advancedmanhunt.gui.json;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.jetbrains.annotations.NotNull;

public interface LogicDelegatingAdvancedGui extends AdvancedGui {
    boolean hasLogicInstance();

    @NotNull
    Object getLogicInstance();
}

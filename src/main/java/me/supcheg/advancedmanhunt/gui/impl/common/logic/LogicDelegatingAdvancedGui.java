package me.supcheg.advancedmanhunt.gui.impl.common.logic;

import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.jetbrains.annotations.NotNull;

public interface LogicDelegatingAdvancedGui extends AdvancedGui {
    @NotNull
    LogicDelegate getLogicDelegate();
}

package me.supcheg.advancedmanhunt.gui.json.structure;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.Duration;
import me.supcheg.advancedmanhunt.gui.api.functional.GuiBackgroundFunction;

@Data
public class GuiBackground {
    private final Duration changePeriod;
    private final GuiBackgroundFunction function;
}

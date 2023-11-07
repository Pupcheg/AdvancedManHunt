package me.supcheg.advancedmanhunt.gui.api.context;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;

@Data
public class ButtonResourceGetContext {
    private final AdvancedGui gui;
    private final AdvancedButton button;
    private final int slot;
}

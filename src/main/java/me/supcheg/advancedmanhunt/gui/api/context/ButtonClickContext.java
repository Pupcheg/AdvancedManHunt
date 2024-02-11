package me.supcheg.advancedmanhunt.gui.api.context;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import me.supcheg.advancedmanhunt.gui.api.ButtonInteractType;
import org.bukkit.entity.Player;

@Data
public class ButtonClickContext {
    private final AdvancedGui gui;
    private final AdvancedButton button;
    private final int slot;
    private final ButtonInteractType interactType;
    private final Player player;
}

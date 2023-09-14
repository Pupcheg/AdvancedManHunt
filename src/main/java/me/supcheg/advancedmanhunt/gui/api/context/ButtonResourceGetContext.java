package me.supcheg.advancedmanhunt.gui.api.context;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.AdvancedButton;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
public class ButtonResourceGetContext {
    private final AdvancedGui gui;
    private final AdvancedButton button;
    private final int slot;
    private final Player player;

    @NotNull
    public Player getPlayer() {
        if (player == null) {
            throw new UnsupportedOperationException(gui + " is not individual");
        }
        return player;
    }
}

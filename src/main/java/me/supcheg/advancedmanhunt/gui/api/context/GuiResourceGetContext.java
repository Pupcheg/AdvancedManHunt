package me.supcheg.advancedmanhunt.gui.api.context;

import lombok.Data;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Data
public class GuiResourceGetContext {
    private final AdvancedGui gui;
    private final Player player;

    @NotNull
    public Player getPlayer() {
        if (player == null) {
            throw new UnsupportedOperationException(gui + " is not individual");
        }
        return player;
    }
}

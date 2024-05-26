package me.supcheg.advancedmanhunt.game.handler;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.gui.ManHuntGameConfigurateGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import org.jetbrains.annotations.NotNull;

public class ManHuntGameConfigHandler extends ManHuntGameHandler {
    private final AdvancedGuiController controller;
    private volatile ManHuntGameConfigurateGui gui;

    public ManHuntGameConfigHandler(@NotNull ManHuntGame game, @NotNull AdvancedGuiController controller) {
        super(game);
        this.controller = controller;
    }

    @NotNull
    public ManHuntGameConfigurateGui getGui() {
        if (gui == null) {
            synchronized (this) {
                if (gui == null) {
                    gui = new ManHuntGameConfigurateGui(controller, game);
                }
            }
        }
        return gui;
    }

    @Override
    public void unregister() {
        super.unregister();

        if (gui != null) {
            synchronized (this) {
                if (gui != null) {
                    gui.unregister();
                    gui = null;
                }
            }
        }
    }
}

package me.supcheg.advancedmanhunt.game.handler;

import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.gui.ManHuntGameConfigureGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import org.jetbrains.annotations.NotNull;

public class ManHuntGameConfigHandler extends ManHuntGameHandler {
    private final AdvancedGuiController controller;
    private volatile ManHuntGameConfigureGui gui;

    public ManHuntGameConfigHandler(@NotNull ManHuntGame game, @NotNull AdvancedGuiController controller) {
        super(game);
        this.controller = controller;
    }

    @NotNull
    public ManHuntGameConfigureGui getGui() {
        if (gui == null) {
            synchronized (this) {
                if (gui == null) {
                    gui = new ManHuntGameConfigureGui(controller, game);
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

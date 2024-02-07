package me.supcheg.advancedmanhunt;

import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;

public interface AdvancedManHuntPlugin {

    String NAME = "AdvancedManHunt";
    String NAMESPACE = "advancedmanhunt";

    @NotNull
    ContainerAdapter getContainerAdapter();

    @NotNull
    CountDownTimerFactory getCountDownTimerFactory();

    @NotNull
    ManHuntGameRepository getGameRepository();

    @NotNull
    GameRegionRepository getGameRegionRepository();

    @NotNull
    PlayerFreezer getPlayerFreezer();

    @NotNull
    PlayerReturner getPlayerReturner();

    @NotNull
    TemplateRepository getTemplateRepository();

    @NotNull
    TemplateLoader getTemplateLoader();

    @NotNull
    AdvancedGuiController getGuiController();
}

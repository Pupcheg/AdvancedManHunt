package me.supcheg.advancedmanhunt;

import com.google.gson.Gson;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateTaskFactory;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.jetbrains.annotations.NotNull;

public interface AdvancedManHuntPlugin {

    String NAME = "AdvancedManHunt";
    String NAMESPACE = "advancedmanhunt";

    @NotNull
    Gson getGson();

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
    EntityRepository<Template, String> getTemplateRepository();

    @NotNull
    TemplateLoader getTemplateLoader();

    @NotNull
    TemplateTaskFactory getTemplateTaskFactory();

    @NotNull
    AdvancedGuiController getGuiController();
}

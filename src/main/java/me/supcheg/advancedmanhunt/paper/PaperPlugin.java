package me.supcheg.advancedmanhunt.paper;

import dagger.Component;
import lombok.CustomLog;
import me.supcheg.advancedmanhunt.bridge.BridgeModule;
import me.supcheg.advancedmanhunt.bridge.BrigadierCommandRegisterer;
import me.supcheg.advancedmanhunt.command.AdvancedManHuntCommand;
import me.supcheg.advancedmanhunt.command.CommandModule;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.config.ConfigModule;
import me.supcheg.advancedmanhunt.game.ManHuntGameModule;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.GuiModule;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.io.IoModule;
import me.supcheg.advancedmanhunt.player.PlayerModule;
import me.supcheg.advancedmanhunt.player.PlayerReturners;
import me.supcheg.advancedmanhunt.region.GameRegionModule;
import me.supcheg.advancedmanhunt.template.TemplateModule;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.WorldGenerators;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Singleton;
import java.io.IOException;

@CustomLog
public class PaperPlugin extends JavaPlugin {

    private PaperPluginApp app;

    @Component(modules = {
            TemplateModule.class,
            WorldGenerators.class,
            GuiModule.class,
            ConfigModule.class,
            ManHuntGameModule.class,
            PlayerModule.class,
            PlayerReturners.class,
            CommandModule.class,
            IoModule.class,
            GameRegionModule.class,
            BridgeModule.class
    })
    @Singleton
    interface PaperPluginApp {
        ConfigLoader configLoader();

        BrigadierCommandRegisterer commandRegisterer();

        AdvancedManHuntCommand advancedmanhuntCommand();

        AdvancedGuiController guiController();

        GamesListGui gamesListGui();

        ContainerAdapter containerAdapter();

        TemplateRepository templateRepository();
    }

    @Override
    public void onEnable() {
        app = DaggerPaperPlugin_PaperPluginApp.create();

        app.configLoader()
                .loadAndSave("config.yml", AdvancedManHuntConfig.class);

        app.advancedmanhuntCommand()
                .register(app.commandRegisterer());

        app.gamesListGui()
                .register(app.guiController());
    }

    @Override
    public void onDisable() {
        try {
            app.containerAdapter()
                    .close();
        } catch (IOException ioe) {
            log.error("An error occurred while closing ContainerAdapter", ioe);
        }

        app.templateRepository()
                .save();
    }
}

package me.supcheg.advancedmanhunt;

import com.google.gson.Gson;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.task.TemplateTaskFactory;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Path;

public interface AdvancedManHuntPlugin {

    String PLUGIN_NAME = "advancedmanhunt";

    @NotNull
    default Path resolveDataPath(@NotNull String path) {
        return resolveDataPath(Path.of(path));
    }

    @NotNull
    Path resolveDataPath(@NotNull Path path);

    @Nullable
    InputStream getResource(@NotNull String name);

    @NotNull
    JavaPlugin getBukkitPlugin();

    @NotNull
    CustomLogger getSLF4JLogger();

    @NotNull
    Gson getGson();

    @NotNull
    ContainerAdapter getContainerAdapter();

    @NotNull
    CountDownTimerFactory getCountDownTimerFactory();

    @NotNull
    ManHuntGameRepository getGameRepository();

    @NotNull
    ManHuntPlayerViewRepository getPlayerViewRepository();

    @NotNull
    GameRegionRepository getGameRegionRepository();

    @NotNull
    PlayerFreezer getPlayerFreezer();

    @NotNull
    TemplateRepository getTemplateRepository();

    @NotNull
    TemplateLoader getTemplateLoader();

    @NotNull
    TemplateTaskFactory getTemplateTaskFactory();

    void addListener(@NotNull Listener listener);
}

package me.supcheg.advancedmanhunt.paper;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.AdvancedManHuntCommandManager;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.config.MessagesConfig;
import me.supcheg.advancedmanhunt.config.SoundsConfig;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.freeze.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.impl.ConfigTemplateRepository;
import me.supcheg.advancedmanhunt.template.impl.ReplacingTemplateLoader;
import me.supcheg.advancedmanhunt.template.task.TemplateTaskFactory;
import me.supcheg.advancedmanhunt.template.task.impl.ChunkyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.template.task.impl.DummyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Getter(onMethod_ = {@Override, @NotNull})
public class PaperPlugin extends JavaPlugin implements AdvancedManHuntPlugin {

    private final ContainerAdapter containerAdapter;
    @Getter(AccessLevel.NONE)
    private CustomLogger logger;
    private Gson gson;
    private CountDownTimerFactory countDownTimerFactory;

    private ManHuntGameRepository gameRepository;
    private ManHuntPlayerViewRepository playerViewRepository;
    private GameRegionRepository gameRegionRepository;

    private PlayerFreezer playerFreezer;

    private TemplateRepository templateRepository;
    private TemplateLoader templateLoader;
    private TemplateTaskFactory templateTaskFactory;

    public PaperPlugin(@NotNull ContainerAdapter containerAdapter) {
        this.containerAdapter = containerAdapter;
    }

    @Override
    public void onEnable() {
        logger = new CustomLogger(super.getSLF4JLogger());
        gson = JsonSerializer.createGson();

        ConfigLoader configLoader = new ConfigLoader(this);
        configLoader.load("config.yml", AdvancedManHuntConfig.class);
        configLoader.load("sounds.yml", SoundsConfig.class);
        configLoader.load("messages.yml", MessagesConfig.class);

        countDownTimerFactory = new DefaultCountDownTimerFactory(this);

        gameRepository = new DefaultManHuntGameRepository(this);
        playerViewRepository = new DefaultManHuntPlayerViewRepository();
        gameRegionRepository = new DefaultGameRegionRepository(this);

        playerFreezer = new DefaultPlayerFreezer(this);

        templateRepository = new ConfigTemplateRepository(this);
        templateLoader = new ReplacingTemplateLoader(this);
        templateTaskFactory = isPluginInstalled("Chunky") ?
                new ChunkyTemplateTaskFactory(this) :
                new DummyTemplateTaskFactory();

        new AdvancedManHuntCommandManager(this).setup();
    }

    @Override
    public void onDisable() {
        gameRegionRepository.close();
    }

    private static boolean isPluginInstalled(@NotNull String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

    @Override
    public void addListener(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @NotNull
    @Override
    public CustomLogger getSLF4JLogger() {
        return logger;
    }

    @NotNull
    @Override
    public PaperPlugin getBukkitPlugin() {
        return this;
    }

    @NotNull
    @Override
    public Path resolveDataPath(@NotNull Path path) {
        return getDataFolder().toPath().resolve(path);
    }

}

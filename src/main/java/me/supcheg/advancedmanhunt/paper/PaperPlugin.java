package me.supcheg.advancedmanhunt.paper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.GameCommand;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.lang.LanguageLoader;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.freeze.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.freeze.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.impl.DefaultContainerAdapter;
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

@Getter(onMethod_ = {@Override, @NotNull})
public class PaperPlugin extends JavaPlugin implements AdvancedManHuntPlugin {

    protected ContainerAdapter containerAdapter;
    @Getter(AccessLevel.NONE)
    protected CustomLogger logger;
    protected Gson gson;
    protected CountDownTimerFactory countDownTimerFactory;

    protected ManHuntGameRepository gameRepository;
    protected ManHuntPlayerViewRepository playerViewRepository;
    protected GameRegionRepository gameRegionRepository;

    protected PlayerFreezer playerFreezer;
    protected PlayerReturner playerReturner;

    protected TemplateRepository templateRepository;
    protected TemplateLoader templateLoader;
    protected TemplateTaskFactory templateTaskFactory;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        containerAdapter = new DefaultContainerAdapter(getFile().toPath(), getDataFolder().toPath());

        logger = new CustomLogger(super.getSLF4JLogger());
        gson = new GsonBuilder().registerTypeAdapterFactory(new JsonSerializer()).create();

        ConfigLoader configLoader = new ConfigLoader(this);
        configLoader.load("config.yml", AdvancedManHuntConfig.class);

        countDownTimerFactory = new DefaultCountDownTimerFactory(this);

        gameRepository = new DefaultManHuntGameRepository(this);
        playerViewRepository = new DefaultManHuntPlayerViewRepository();
        gameRegionRepository = new DefaultGameRegionRepository(this);

        playerFreezer = new DefaultPlayerFreezer(this);

        String returnerType = AdvancedManHuntConfig.Game.PlayerReturner.TYPE;
        String returnerArgument = AdvancedManHuntConfig.Game.PlayerReturner.ARGUMENT;
        playerReturner = switch (returnerType.toLowerCase()) {
            case "teleport", "tp", "teleporting" -> new TeleportingPlayerReturner(returnerArgument);
            case "custom", "event" -> new EventInitializingPlayerReturner(this);
            default -> throw new IllegalArgumentException(returnerType);
        };

        templateRepository = new ConfigTemplateRepository(this);
        templateLoader = new ReplacingTemplateLoader(this);
        templateTaskFactory = isPluginInstalled("Chunky") ?
                new ChunkyTemplateTaskFactory(this, r -> Bukkit.getScheduler().runTask(this, r)) :
                new DummyTemplateTaskFactory();

        Bukkit.getCommandMap().register(PLUGIN_NAME, new GameCommand(this));
        Bukkit.getCommandMap().register(PLUGIN_NAME, new TemplateCommand(this));
        new LanguageLoader(this).setup();

        logger.debugIfEnabled("Enabled in {} ms", System.currentTimeMillis() - startTime);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        long startTime = System.currentTimeMillis();

        for (var declaredField : getClass().getDeclaredFields()) {
            if (declaredField.canAccess(this)) {
                Object value = declaredField.get(this);

                if (value instanceof AutoCloseable closeable) {
                    try {
                        closeable.close();
                        logger.debugIfEnabled("Closed {}", value.getClass().getSimpleName());
                    } catch (Exception e) {
                        logger.error("An error occurred while closing: {}", value, e);
                    }
                }
            }
        }

        logger.debugIfEnabled("Disabled in {} ms", System.currentTimeMillis() - startTime);
    }

    protected boolean isPluginInstalled(@NotNull String name) {
        var plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            if (!plugin.isEnabled()) {
                throw new IllegalStateException(name + " is installed, but is not loaded");
            }
            logger.debugIfEnabled("Found enabled '{}' plugin", name);
            return true;
        }
        logger.debugIfEnabled("Not found '{}' plugin", name);
        return false;
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
}

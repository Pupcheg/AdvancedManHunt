package me.supcheg.advancedmanhunt.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.CustomLog;
import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.GameCommand;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.api.render.ConfigTextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.controller.DefaultAdvancedGuiController;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.mod.ModSetup;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.storage.EntityRepository;
import me.supcheg.advancedmanhunt.storage.Repositories;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateTaskFactory;
import me.supcheg.advancedmanhunt.template.impl.ChunkyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.template.impl.DummyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.template.impl.ReplacingTemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.concurrent.PluginBasedSyncExecutor;
import me.supcheg.advancedmanhunt.util.concurrent.impl.DefaultFuturesBuilderFactory;
import me.supcheg.bridge.Bridge;
import me.supcheg.bridge.BridgeHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executor;

@CustomLog
@Getter(onMethod_ = {@Override, @NotNull})
public class PaperPlugin extends JavaPlugin implements AdvancedManHuntPlugin {

    private ContainerAdapter containerAdapter;
    private Gson gson;
    private CountDownTimerFactory countDownTimerFactory;

    private ManHuntGameRepository gameRepository;
    private GameRegionRepository gameRegionRepository;

    private PlayerFreezer playerFreezer;
    private PlayerReturner playerReturner;

    private EntityRepository<Template, String> templateRepository;
    private TemplateLoader templateLoader;
    private TemplateTaskFactory templateTaskFactory;

    private AdvancedGuiController guiController;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        containerAdapter = new PaperContainerAdapter(getFile().toPath(), getDataFolder().toPath());
        new ModSetup(containerAdapter).setup();

        Bridge bridge = BridgeHolder.getInstance();

        Executor syncExecutor = new PluginBasedSyncExecutor(this);
        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(this);

        gson = new GsonBuilder().registerTypeAdapterFactory(new JsonSerializer()).create();

        ConfigLoader configLoader = new ConfigLoader(containerAdapter);
        configLoader.load("config.yml", AdvancedManHuntConfig.class);

        countDownTimerFactory = new DefaultCountDownTimerFactory(this);

        gameRegionRepository = new DefaultGameRegionRepository(containerAdapter, eventListenerRegistry);

        playerFreezer = new DefaultPlayerFreezer(eventListenerRegistry);

        String returnerType = AdvancedManHuntConfig.Game.PlayerReturner.TYPE;
        String returnerArgument = AdvancedManHuntConfig.Game.PlayerReturner.ARGUMENT;
        playerReturner = switch (returnerType.toLowerCase()) {
            case "teleport", "tp", "teleporting" -> new TeleportingPlayerReturner(returnerArgument);
            case "custom", "event" -> new EventInitializingPlayerReturner();
            default -> throw new IllegalArgumentException(returnerType);
        };

        templateRepository = Repositories.pathSerializing(containerAdapter.resolveData("templates.json"), gson,
                Template.class, Template::getName);
        templateLoader = new ReplacingTemplateLoader();
        templateTaskFactory = isPluginInstalled("Chunky") ?
                new ChunkyTemplateTaskFactory(containerAdapter, templateRepository, syncExecutor) :
                new DummyTemplateTaskFactory();

        gameRepository = new DefaultManHuntGameRepository(gameRegionRepository,
                templateRepository, templateLoader,
                countDownTimerFactory,
                playerReturner, playerFreezer,
                eventListenerRegistry,
                new DefaultFuturesBuilderFactory(syncExecutor)
        );

        ConfigTextureWrapper textureWrapper = new ConfigTextureWrapper(containerAdapter);
        textureWrapper.load("resources.json");

        guiController = new DefaultAdvancedGuiController(textureWrapper, bridge::sendTitle, this);
        new GamesListGui(gameRepository, eventListenerRegistry).register(guiController);

        LiteralArgumentBuilder<BukkitBrigadierCommandSource> mainCommand = LiteralArgumentBuilder.literal(NAMESPACE);
        new GameCommand(templateRepository, gameRepository, guiController).append(mainCommand);
        new TemplateCommand(templateRepository, templateTaskFactory, gson).append(mainCommand);
        bridge.registerBrigadierCommand(mainCommand);

        log.debugIfEnabled("Enabled in {} ms", System.currentTimeMillis() - startTime);
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        long startTime = System.currentTimeMillis();

        for (Field declaredField : getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(declaredField.getModifiers()) && declaredField.canAccess(this)) {
                Object value = declaredField.get(this);

                if (value instanceof AutoCloseable closeable) {
                    try {
                        closeable.close();
                        log.debugIfEnabled("Closed {}", value.getClass().getSimpleName());
                    } catch (Exception e) {
                        log.error("An error occurred while closing: {}", value, e);
                    }
                }
            }
        }

        log.debugIfEnabled("Disabled in {} ms", System.currentTimeMillis() - startTime);
    }

    protected boolean isPluginInstalled(@NotNull String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            if (!plugin.isEnabled()) {
                throw new IllegalStateException(name + " is installed, but is not loaded");
            }
            log.debugIfEnabled("Found enabled '{}' plugin", name);
            return true;
        }
        log.debugIfEnabled("Not found '{}' plugin", name);
        return false;
    }
}

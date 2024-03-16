package me.supcheg.advancedmanhunt.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.CustomLog;
import lombok.Getter;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.command.DebugCommand;
import me.supcheg.advancedmanhunt.command.GameCommand;
import me.supcheg.advancedmanhunt.command.TemplateCommand;
import me.supcheg.advancedmanhunt.command.service.TemplateService;
import me.supcheg.advancedmanhunt.concurrent.PluginBasedSyncExecutor;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.GamesListGui;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.gui.impl.common.texture.ConfigTextureWrapper;
import me.supcheg.advancedmanhunt.gui.impl.inventory.InventoryGuiController;
import me.supcheg.advancedmanhunt.gui.json.JsonGuiLoader;
import me.supcheg.advancedmanhunt.injector.Bridge;
import me.supcheg.advancedmanhunt.injector.Injector;
import me.supcheg.advancedmanhunt.injector.item.ItemStackWrapperFactory;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import me.supcheg.advancedmanhunt.io.DefaultContainerAdapter;
import me.supcheg.advancedmanhunt.player.PlayerFreezer;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.PlayerReturners;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.template.WorldGenerator;
import me.supcheg.advancedmanhunt.template.impl.BukkitWorldGenerator;
import me.supcheg.advancedmanhunt.template.impl.ChunkyWorldGenerator;
import me.supcheg.advancedmanhunt.template.impl.DefaultTemplateRepository;
import me.supcheg.advancedmanhunt.template.impl.ReplacingTemplateLoader;
import me.supcheg.advancedmanhunt.timer.CountDownTimerFactory;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
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
    private CountDownTimerFactory countDownTimerFactory;

    private ManHuntGameRepository gameRepository;
    private GameRegionRepository gameRegionRepository;

    private PlayerFreezer playerFreezer;
    private PlayerReturner playerReturner;

    private TemplateRepository templateRepository;
    private TemplateLoader templateLoader;

    private AdvancedGuiController guiController;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        containerAdapter = new DefaultContainerAdapter(getFile().toPath(), getDataFolder().toPath());

        Bridge bridge = Injector.getBridge();

        Executor syncExecutor = new PluginBasedSyncExecutor(this);
        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(this);

        ConfigLoader configLoader = new ConfigLoader(containerAdapter);
        configLoader.loadAndSave("config.yml", AdvancedManHuntConfig.class);

        countDownTimerFactory = new DefaultCountDownTimerFactory(this);

        gameRegionRepository = new DefaultGameRegionRepository(eventListenerRegistry);

        playerFreezer = new DefaultPlayerFreezer(eventListenerRegistry);
        playerReturner = PlayerReturners.loadPlayerReturner();

        templateRepository = new DefaultTemplateRepository(containerAdapter);
        templateLoader = new ReplacingTemplateLoader();

        ConfigTextureWrapper textureWrapper = new ConfigTextureWrapper(containerAdapter);
        textureWrapper.load("resources.json");

        ItemStackWrapperFactory itemStackWrapperFactory = bridge.getItemStackWrapperFactory();

        guiController = new InventoryGuiController(itemStackWrapperFactory,
                textureWrapper,
                new JsonGuiLoader(containerAdapter),
                this
        );

        gameRepository = new DefaultManHuntGameRepository(gameRegionRepository,
                templateRepository, templateLoader,
                countDownTimerFactory,
                playerReturner, playerFreezer,
                eventListenerRegistry,
                syncExecutor,
                guiController
        );

        new GamesListGui(gameRepository, eventListenerRegistry).load(guiController);

        WorldGenerator generator = isPluginInstalled("Chunky") ? new ChunkyWorldGenerator() : new BukkitWorldGenerator();
        TemplateService templateService = new TemplateService(templateRepository, generator, syncExecutor, containerAdapter);

        LiteralArgumentBuilder<BukkitBrigadierCommandSource> mainCommand = LiteralArgumentBuilder.literal(NAMESPACE);
        new GameCommand(templateRepository, gameRepository, guiController).append(mainCommand);
        new TemplateCommand(templateService).append(mainCommand);
        new DebugCommand().appendIfEnabled(mainCommand);
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

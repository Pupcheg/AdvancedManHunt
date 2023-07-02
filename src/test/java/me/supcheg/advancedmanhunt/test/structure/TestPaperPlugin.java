package me.supcheg.advancedmanhunt.test.structure;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.logging.CustomLogger;
import me.supcheg.advancedmanhunt.paper.PaperPlugin;
import me.supcheg.advancedmanhunt.player.freeze.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.player.impl.TeleportingPlayerReturner;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.template.task.impl.DummyTemplateTaskFactory;
import me.supcheg.advancedmanhunt.test.structure.template.DummyTemplateLoader;
import me.supcheg.advancedmanhunt.test.structure.template.DummyTemplateRepository;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

@Getter
public class TestPaperPlugin extends PaperPlugin {

    protected CommandDispatcher<BukkitBrigadierCommandSource> commandDispatcher;

    @NotNull
    @Contract(" -> new")
    public static TestPaperPlugin load() {
        return MockBukkit.loadSimple(TestPaperPlugin.class);
    }

    @Override
    public void onEnable() {
        containerAdapter = new DummyContainerAdapter();

        logger = new CustomLogger(LoggerFactory.getLogger(TestPaperPlugin.class));
        gson = new GsonBuilder().registerTypeAdapterFactory(new JsonSerializer()).create();

        countDownTimerFactory = new DefaultCountDownTimerFactory(this);

        gameRepository = new DefaultManHuntGameRepository(this);
        playerViewRepository = new DefaultManHuntPlayerViewRepository();
        gameRegionRepository = new DefaultGameRegionRepository(this);

        playerFreezer = new DefaultPlayerFreezer(this);

        playerReturner = new TeleportingPlayerReturner("world[spawn]");

        templateRepository = new DummyTemplateRepository();
        templateLoader = new DummyTemplateLoader();
        templateTaskFactory = new DummyTemplateTaskFactory();

        commandDispatcher = new TestCommandDispatcher(this);
    }
}

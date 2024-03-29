package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.structure.DummyPlayerReturner;
import me.supcheg.advancedmanhunt.structure.DynamicRepository;
import me.supcheg.advancedmanhunt.structure.template.DummyTemplateLoader;
import me.supcheg.advancedmanhunt.structure.template.TemplateMock;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.concurrent.PluginBasedSyncExecutor;
import me.supcheg.advancedmanhunt.util.concurrent.impl.DefaultFuturesBuilderFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static me.supcheg.advancedmanhunt.util.ThreadSafeRandom.randomUniqueId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManHuntGamePlayersTest {
    private static final int HUNTERS_LIMIT = AdvancedManHuntConfig.Game.DefaultConfig.MAX_HUNTERS;
    private static final int SPECTATORS_LIMIT = AdvancedManHuntConfig.Game.DefaultConfig.MAX_SPECTATORS;

    private ManHuntGame game;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        Plugin dummyPlugin = MockBukkit.createMockPlugin();

        ContainerAdapter containerAdapter = new DummyContainerAdapter();

        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(dummyPlugin);
        Executor syncExecutor = new PluginBasedSyncExecutor(dummyPlugin);

        ManHuntGameRepository gameRepository = new DefaultManHuntGameRepository(
                new DefaultGameRegionRepository(containerAdapter, eventListenerRegistry),
                new DynamicRepository<>(Template::getName, TemplateMock::new),
                new DummyTemplateLoader(),
                new DefaultCountDownTimerFactory(dummyPlugin),
                new DummyPlayerReturner(),
                new DefaultPlayerFreezer(eventListenerRegistry),
                eventListenerRegistry,
                new DefaultFuturesBuilderFactory(syncExecutor)
        );
        game = gameRepository.create(randomUniqueId());
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void sequentialAddTest() {
        assertSame(ManHuntRole.RUNNER, game.addMember(randomUniqueId()));
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertSame(ManHuntRole.HUNTER, game.addMember(randomUniqueId()));
        }
        for (int i = 0; i < SPECTATORS_LIMIT; i++) {
            assertSame(ManHuntRole.SPECTATOR, game.addMember(randomUniqueId()));
        }
        assertNull(game.addMember(randomUniqueId()));
    }

    @Test
    void huntersOverflowTest() {
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertTrue(game.addMember(randomUniqueId(), ManHuntRole.HUNTER));
        }
        assertFalse(game.addMember(randomUniqueId(), ManHuntRole.HUNTER));
    }

    @Test
    void playersLimitTest() {
        int count = 0;
        while (game.canAcceptPlayer()) {
            game.addMember(randomUniqueId());
            count++;
        }
        assertEquals(ManHuntRole.SPECTATOR, game.addMember(randomUniqueId()));

        assertEquals(HUNTERS_LIMIT + 1, count);
    }

    @Test
    void spectatorsLimitTest() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addMember(randomUniqueId(), ManHuntRole.SPECTATOR);
            count++;
        }
        assertFalse(game.addMember(randomUniqueId(), ManHuntRole.SPECTATOR));

        assertEquals(SPECTATORS_LIMIT, count);
    }
}

package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.template.TemplateRepository;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import me.supcheg.advancedmanhunt.util.concurrent.PluginBasedSyncExecutor;
import me.supcheg.advancedmanhunt.util.concurrent.impl.DefaultFuturesBuilderFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.util.ThreadSafeRandom.randomUniqueId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class ManHuntGamePlayersTest {
    private ManHuntGame game;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        Plugin dummyPlugin = MockBukkit.createMockPlugin();

        ContainerAdapter containerAdapter = Mockito.mock(ContainerAdapter.class);
        TemplateLoader templateLoader = Mockito.mock(TemplateLoader.class);
        Mockito.when(templateLoader.loadTemplate(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(dummyPlugin);
        Executor syncExecutor = new PluginBasedSyncExecutor(dummyPlugin);
        TemplateRepository templateRepository = Mockito.mock(TemplateRepository.class);

        ManHuntGameRepository gameRepository = new DefaultManHuntGameRepository(
                new DefaultGameRegionRepository(eventListenerRegistry),
                templateRepository,
                templateLoader,
                new DefaultCountDownTimerFactory(dummyPlugin),
                Mockito.mock(PlayerReturner.class),
                new DefaultPlayerFreezer(eventListenerRegistry),
                eventListenerRegistry,
                new DefaultFuturesBuilderFactory(syncExecutor),
                Mockito.mock(AdvancedGuiController.class)
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
        for (int i = 0; i < config().game.configDefaults.maxHunters; i++) {
            assertSame(ManHuntRole.HUNTER, game.addMember(randomUniqueId()));
        }
        for (int i = 0; i < config().game.configDefaults.maxSpectators; i++) {
            assertSame(ManHuntRole.SPECTATOR, game.addMember(randomUniqueId()));
        }
        assertNull(game.addMember(randomUniqueId()));
    }

    @Test
    void huntersOverflowTest() {
        for (int i = 0; i < config().game.configDefaults.maxHunters; i++) {
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

        assertEquals(config().game.configDefaults.maxHunters + 1, count);
    }

    @Test
    void spectatorsLimitTest() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addMember(randomUniqueId(), ManHuntRole.SPECTATOR);
            count++;
        }
        assertFalse(game.addMember(randomUniqueId(), ManHuntRole.SPECTATOR));

        assertEquals(config().game.configDefaults.maxSpectators, count);
    }
}

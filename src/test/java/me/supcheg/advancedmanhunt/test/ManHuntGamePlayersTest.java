package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.freeze.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.region.ContainerAdapter;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.structure.DummyPlayerReturner;
import me.supcheg.advancedmanhunt.structure.template.DummyTemplateLoader;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManHuntGamePlayersTest {
    private static final int HUNTERS_LIMIT = 3;
    private static final int SPECTATORS_LIMIT = 15;

    private ServerMock mock;
    private ManHuntGame game;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
        Plugin dummyPlugin = MockBukkit.createMockPlugin();
        ContainerAdapter containerAdapter = new DummyContainerAdapter();
        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(dummyPlugin);
        ManHuntGameRepository gameRepository = new DefaultManHuntGameRepository(
                new DefaultGameRegionRepository(containerAdapter, JsonSerializer.createGson(), eventListenerRegistry),
                new DummyTemplateLoader(),
                new DefaultCountDownTimerFactory(dummyPlugin),
                new DummyPlayerReturner(),
                new DefaultPlayerFreezer(eventListenerRegistry),
                new DefaultManHuntPlayerViewRepository(),
                eventListenerRegistry
        );
        game = gameRepository.create(newPlayerView(), HUNTERS_LIMIT, SPECTATORS_LIMIT);
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void sequentialAddTest() {
        assertSame(ManHuntRole.RUNNER, game.addPlayer(newPlayerView()));
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertSame(ManHuntRole.HUNTER, game.addPlayer(newPlayerView()));
        }
        for (int i = 0; i < SPECTATORS_LIMIT; i++) {
            assertSame(ManHuntRole.SPECTATOR, game.addPlayer(newPlayerView()));
        }
        assertNull(game.addPlayer(newPlayerView()));
    }

    @Test
    void huntersOverflowTest() {
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertTrue(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
        }
        assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
    }

    @Test
    void playersLimitTest() {
        int count = 0;
        while (game.canAcceptPlayer()) {
            game.addPlayer(newPlayerView());
            count++;
        }
        assertEquals(ManHuntRole.SPECTATOR, game.addPlayer(newPlayerView()));

        assertEquals(HUNTERS_LIMIT + 1, count);
    }

    @Test
    void spectatorsLimitTest() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR);
            count++;
        }
        assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR));

        assertEquals(SPECTATORS_LIMIT, count);
    }

    @NotNull
    @Contract(" -> new")
    private ManHuntPlayerView newPlayerView() {
        return new DefaultManHuntPlayerView(mock.addPlayer().getUniqueId());
    }
}

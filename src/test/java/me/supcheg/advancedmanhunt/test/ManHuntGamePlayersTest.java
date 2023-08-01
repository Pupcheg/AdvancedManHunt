package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameRepository;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.player.freeze.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.structure.DummyPlayerReturner;
import me.supcheg.advancedmanhunt.structure.template.DummyTemplateLoader;
import me.supcheg.advancedmanhunt.timer.impl.DefaultCountDownTimerFactory;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

class ManHuntGamePlayersTest {
    private static final int HUNTERS_LIMIT = 3;
    private static final int SPECTATORS_LIMIT = 15;

    private ManHuntGame game;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        Plugin dummyPlugin = MockBukkit.createMockPlugin();

        ContainerAdapter containerAdapter = new DummyContainerAdapter();
        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(dummyPlugin);
        ManHuntGameRepository gameRepository = new DefaultManHuntGameRepository(
                new DefaultGameRegionRepository(containerAdapter, eventListenerRegistry),
                new DummyTemplateLoader(),
                new DefaultCountDownTimerFactory(dummyPlugin),
                new DummyPlayerReturner(),
                new DefaultPlayerFreezer(eventListenerRegistry),
                eventListenerRegistry
        );
        game = gameRepository.create(randomUUID(), HUNTERS_LIMIT, SPECTATORS_LIMIT);
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void sequentialAddTest() {
        assertSame(ManHuntRole.RUNNER, game.addMember(randomUUID()));
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertSame(ManHuntRole.HUNTER, game.addMember(randomUUID()));
        }
        for (int i = 0; i < SPECTATORS_LIMIT; i++) {
            assertSame(ManHuntRole.SPECTATOR, game.addMember(randomUUID()));
        }
        assertNull(game.addMember(randomUUID()));
    }

    @Test
    void huntersOverflowTest() {
        for (int i = 0; i < HUNTERS_LIMIT; i++) {
            assertTrue(game.addMember(randomUUID(), ManHuntRole.HUNTER));
        }
        assertFalse(game.addMember(randomUUID(), ManHuntRole.HUNTER));
    }

    @Test
    void playersLimitTest() {
        int count = 0;
        while (game.canAcceptPlayer()) {
            game.addMember(randomUUID());
            count++;
        }
        assertEquals(ManHuntRole.SPECTATOR, game.addMember(randomUUID()));

        assertEquals(HUNTERS_LIMIT + 1, count);
    }

    @Test
    void spectatorsLimitTest() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addMember(randomUUID(), ManHuntRole.SPECTATOR);
            count++;
        }
        assertFalse(game.addMember(randomUUID(), ManHuntRole.SPECTATOR));

        assertEquals(SPECTATORS_LIMIT, count);
    }
}

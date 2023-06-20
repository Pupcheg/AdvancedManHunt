package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerView;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManHuntGamePlayersTest {
    private ManHuntGame game;

    @BeforeAll
    static void beforeAll() {
        MockBukkit.mock();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setup() {
        game = TestPaperPlugin.load()
                .getGameRepository()
                .create(newPlayerView(), 1, 1);
    }

    @Test
    void sequentialAdd() {
        assertSame(ManHuntRole.RUNNER, game.addPlayer(newPlayerView()));
        assertSame(ManHuntRole.HUNTER, game.addPlayer(newPlayerView()));
        assertSame(ManHuntRole.SPECTATOR, game.addPlayer(newPlayerView()));
        assertNull(game.addPlayer(newPlayerView()));
    }

    @Test
    void huntersOverflow() {
        assertTrue(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
        assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
    }

    @Test
    void maxPlayers() {
        int count = 0;
        while (game.canAcceptPlayer()) {
            game.addPlayer(newPlayerView());
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    void maxSpectators() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR);
            count++;
        }
        assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR));

        assertEquals(1, count);
    }

    @NotNull
    @Contract(" -> new")
    private ManHuntPlayerView newPlayerView() {
        return new DefaultManHuntPlayerView(UUID.randomUUID());
    }
}

package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntRole;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.impl.DefaultManHuntPlayerView;
import me.supcheg.advancedmanhunt.test.structure.InjectingPaperPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
        game = InjectingPaperPlugin.load()
                .getGameRepository()
                .create(newPlayerView(), 1, 1);
    }

    @Test
    void sequentialAdd() {
        Assertions.assertSame(ManHuntRole.RUNNER, game.addPlayer(newPlayerView()));
        Assertions.assertSame(ManHuntRole.HUNTER, game.addPlayer(newPlayerView()));
        Assertions.assertSame(ManHuntRole.SPECTATOR, game.addPlayer(newPlayerView()));
        Assertions.assertNull(game.addPlayer(newPlayerView()));
    }

    @Test
    void huntersOverflow() {
        Assertions.assertTrue(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
        Assertions.assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.HUNTER));
    }

    @Test
    void maxPlayers() {
        int count = 0;
        while (game.canAcceptPlayer()) {
            game.addPlayer(newPlayerView());
            count++;
        }

        Assertions.assertEquals(2, count);
    }

    @Test
    void maxSpectators() {
        int count = 0;
        while (game.canAcceptSpectator()) {
            game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR);
            count++;
        }
        Assertions.assertFalse(game.addPlayer(newPlayerView(), ManHuntRole.SPECTATOR));

        Assertions.assertEquals(1, count);
    }

    @NotNull
    @Contract(" -> new")
    private ManHuntPlayerView newPlayerView() {
        return new DefaultManHuntPlayerView(UUID.randomUUID());
    }
}

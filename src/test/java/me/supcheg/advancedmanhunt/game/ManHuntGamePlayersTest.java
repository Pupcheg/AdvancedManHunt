package me.supcheg.advancedmanhunt.game;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import me.supcheg.advancedmanhunt.bridge.impl.safe.CustomEventListenerRegistry;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.mock.MockBukkitUtilExtension;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.template.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.random.ThreadSafeRandom.randomUniqueId;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockBukkitExtension.class, MockBukkitUtilExtension.class})
class ManHuntGamePlayersTest {
    private ManHuntGame game;

    @BeforeEach
    void setup() {
        ManHuntGameService service = new ManHuntGameService(
                new DefaultManHuntGameRepository(),
                new DefaultGameRegionRepository(),
                Mockito.mock(TemplateService.class),
                Mockito.mock(PlayerReturner.class),
                new DefaultPlayerFreezer(),
                Mockito.mock(AdvancedGuiController.class),
                new CustomEventListenerRegistry()
        );

        game = service.createGame(randomUniqueId());
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
}

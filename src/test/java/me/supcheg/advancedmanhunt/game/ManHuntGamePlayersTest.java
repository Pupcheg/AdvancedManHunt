package me.supcheg.advancedmanhunt.game;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.game.impl.DefaultManHuntGameRepository;
import me.supcheg.advancedmanhunt.gui.api.AdvancedGuiController;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.service.TemplateService;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.random.ThreadSafeRandom.randomUniqueId;
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

        TemplateLoader templateLoader = Mockito.mock(TemplateLoader.class);
        Mockito.when(templateLoader.loadTemplate(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        TemplateService templateService = Mockito.mock(TemplateService.class);

        ManHuntGameRepository gameRepository = new DefaultManHuntGameRepository(
                new DefaultGameRegionRepository(),
                templateService,
                Mockito.mock(PlayerReturner.class),
                new DefaultPlayerFreezer(),
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
}

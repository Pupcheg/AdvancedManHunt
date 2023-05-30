package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.game.GameState;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.ManHuntGameConfiguration;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerView;
import me.supcheg.advancedmanhunt.player.ManHuntPlayerViewRepository;
import me.supcheg.advancedmanhunt.template.TemplateLoader;
import me.supcheg.advancedmanhunt.test.structure.DummySpawnLocationFinder;
import me.supcheg.advancedmanhunt.test.structure.InjectingPaperPlugin;
import me.supcheg.advancedmanhunt.test.structure.template.DummyTemplate;
import me.supcheg.advancedmanhunt.test.structure.template.DummyTemplateLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ManHuntGameInitializeTest {

    private ServerMock mock;

    private AdvancedManHuntPlugin plugin;

    private PlayerMock player1;
    private PlayerMock player2;

    private ManHuntPlayerView playerView1;
    private ManHuntPlayerView playerView2;

    private ManHuntGame game;
    private ManHuntGameConfiguration configuration;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
        plugin = InjectingPaperPlugin.load()
                .modifyField(TemplateLoader.class, DummyTemplateLoader.INSTANCE);

        player1 = mock.addPlayer("Ertemaman-1");
        player2 = mock.addPlayer("Ertemaman-2");

        ManHuntPlayerViewRepository playerViewRepository = plugin.getPlayerViewRepository();
        playerView1 = playerViewRepository.get(player1);
        playerView2 = playerViewRepository.get(player2);

        game = plugin.getGameRepository().create(playerView1, 5, 5);

        configuration = ManHuntGameConfiguration.builder()
                .overworldTemplate(DummyTemplate.INSTANCE)
                .netherTemplate(DummyTemplate.INSTANCE)
                .endTemplate(DummyTemplate.INSTANCE)
                .spawnLocationFinder(DummySpawnLocationFinder.INSTANCE)
                .build();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void notOnlineTest() {
        game.addPlayer(playerView1);
        game.addPlayer(playerView2);

        player1.disconnect();

        Assertions.assertThrows(
                Exception.class,
                () -> game.start(configuration)
        );
    }

    @Test
    void initializeGame() {
        game.addPlayer(playerView1);
        game.addPlayer(playerView2);

        game.start(configuration);

        Assertions.assertSame(GameState.START, game.getState());
    }

    @Test
    void leaveAfterInitialize() {
        initializeGame();

        player1.disconnect();

        Assertions.assertTrue(game.getState().upperOrEquals(GameState.STOP));
    }
}

package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventInitializingPlayerReturnerTest {

    private ServerMock mock;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void test() {
        var plugin = TestPaperPlugin.load();
        var playerReturner = new EventInitializingPlayerReturner(plugin);

        AtomicBoolean isHandled = new AtomicBoolean();
        plugin.addListener(new Listener() {
            @EventHandler
            public void onPlayerReturnerInitialize(@NotNull PlayerReturnerInitializeEvent event) {
                event.setPlayerReturner(player -> isHandled.set(true));
            }
        });

        playerReturner.returnPlayer(mock.addPlayer());

        assertTrue(isHandled.get());
    }
}

package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventInitializingPlayerReturnerTest {

    private ServerMock mock;
    private EventInitializingPlayerReturner playerReturner;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
        playerReturner = new EventInitializingPlayerReturner();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void test() {
        AtomicInteger initTimes = new AtomicInteger();
        AtomicInteger handledTimes = new AtomicInteger();

        new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin())
                .addListener(new Listener() {
                    @EventHandler
                    public void onPlayerReturnerInitialize(@NotNull PlayerReturnerInitializeEvent event) {
                        initTimes.getAndIncrement();
                        event.setPlayerReturner(player -> handledTimes.getAndIncrement());
                    }
                });

        Player player = mock.addPlayer();
        playerReturner.returnPlayer(player);
        playerReturner.returnPlayer(player);

        assertEquals(1, initTimes.get());
        assertEquals(2, handledTimes.get());
    }
}

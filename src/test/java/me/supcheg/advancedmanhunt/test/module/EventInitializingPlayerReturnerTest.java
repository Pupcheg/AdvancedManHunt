package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.AdvancedManHuntPlugin;
import me.supcheg.advancedmanhunt.event.PlayerReturnerInitializeEvent;
import me.supcheg.advancedmanhunt.player.PlayerReturner;
import me.supcheg.advancedmanhunt.player.impl.EventInitializingPlayerReturner;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
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
        AdvancedManHuntPlugin plugin = TestPaperPlugin.load();
        PlayerReturner playerReturner = new EventInitializingPlayerReturner(plugin);

        AtomicInteger initTimes = new AtomicInteger();
        AtomicInteger handledTimes = new AtomicInteger();
        plugin.addListener(new Listener() {
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

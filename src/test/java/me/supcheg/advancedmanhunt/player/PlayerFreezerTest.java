package me.supcheg.advancedmanhunt.player;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.event.EventListenerRegistry;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerFreezerTest {

    private PlayerFreezer playerFreezer;
    private Player player;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        EventListenerRegistry eventListenerRegistry = new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin());
        playerFreezer = new DefaultPlayerFreezer(eventListenerRegistry);
        player = mock.addPlayer();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void frozenWithoutGroupTest() {
        playerFreezer.freeze(player);
        assertFrozen(player);

        playerFreezer.unfreeze(player);
        assertNotFrozen(player);
    }

    @Test
    void frozenWithOneGroupTest() {
        FreezeGroup group = playerFreezer.newFreezeGroup();

        group.add(player);
        assertFrozen(player);

        group.remove(player);
        assertNotFrozen(player);
    }

    @Test
    void frozenWithTwoGroupsTest() {
        FreezeGroup firstGroup = playerFreezer.newFreezeGroup();
        FreezeGroup secondGroup = playerFreezer.newFreezeGroup();

        firstGroup.add(player);
        secondGroup.add(player);
        assertFrozen(player);

        firstGroup.remove(player);
        assertFrozen(player);

        secondGroup.remove(player);
        assertNotFrozen(player);
    }

    @Test
    void frozenWithOneGroupAndWithoutGroupTest() {
        FreezeGroup freezeGroup = playerFreezer.newFreezeGroup();

        playerFreezer.freeze(player);
        freezeGroup.add(player);
        assertFrozen(player);

        playerFreezer.unfreeze(player);
        assertFrozen(player);

        freezeGroup.remove(player);
        assertNotFrozen(player);
    }

    private void assertFrozen(@NotNull Player player) {
        assertTrue(playerFreezer.isFrozen(player));
    }

    private void assertNotFrozen(@NotNull Player player) {
        assertFalse(playerFreezer.isFrozen(player));
    }
}

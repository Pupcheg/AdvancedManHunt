package me.supcheg.advancedmanhunt.player;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import be.seeseemelk.mockbukkit.MockBukkitInject;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.mock.MockBukkitUtilExtension;
import me.supcheg.advancedmanhunt.player.impl.DefaultPlayerFreezer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockBukkitExtension.class, MockBukkitUtilExtension.class})
class PlayerFreezerTest {

    PlayerFreezer playerFreezer;
    Player player;

    @BeforeEach
    void setup(@MockBukkitInject ServerMock mock) {
        playerFreezer = new DefaultPlayerFreezer();
        player = mock.addPlayer();
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

package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static me.supcheg.advancedmanhunt.util.LocationParser.parseLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationParserTest {

    private World world;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        world = WorldCreator.name("world").createWorld();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void spawnTest() {
        assertParseResult(world.getSpawnLocation(), "world[spawn]");
    }

    @Test
    void spawnWithDirectionTest() {
        Location spawnWithDirection = world.getSpawnLocation();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        assertParseResult(spawnWithDirection, "world[spawn, 180, -25.5]");
    }

    @Test
    void locationWithoutDirectionTest() {
        assertParseResult(new Location(world, 20.5, .33, -30), "world[20.5, .33, -30]");
    }

    @Test
    void locationWithDirectionTest() {
        assertParseResult(new Location(world, 10, 10, 10, .55f, -0.5f), "world[10, 10, 10, .55, -0.5]");
    }

    private void assertParseResult(@NotNull Location expected, @NotNull String serialized) {
        assertEquals(expected, parseLocation(serialized));
    }

}

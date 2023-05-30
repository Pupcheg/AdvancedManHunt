package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static me.supcheg.advancedmanhunt.util.LocationParser.parseLocation;

class LocationParserTest {

    @BeforeEach
    void setup() {
        MockBukkit.mock();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void test() {
        World world = WorldCreator.name("world").createWorld();
        Assertions.assertNotNull(world);

        Location spawnWithDirection = world.getSpawnLocation().clone();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        Map<String, Location> raw2expected = Map.of(
                "world[spawn]", world.getSpawnLocation(),
                "world[spawn, 180, -25.5]", spawnWithDirection,
                "world[0, 60, 0]", new Location(world, 0, 60, 0),
                "world[0, 60, 0, 90, 180]", new Location(world, 0, 60, 0, 90, 180)
        );

        for (var entry : raw2expected.entrySet()) {
            Assertions.assertEquals(entry.getValue(), parseLocation(entry.getKey()));
            Assertions.assertEquals(entry.getValue(), parseLocation(entry.getKey().replace(" ", "")));
        }
    }

    @Test
    void failTest() {
        Assertions.assertThrows(Exception.class, () -> parseLocation("world[spawn]"));
        Assertions.assertThrows(Exception.class, () -> parseLocation("world[spawn}"));
    }

}

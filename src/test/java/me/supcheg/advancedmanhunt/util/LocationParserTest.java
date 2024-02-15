package me.supcheg.advancedmanhunt.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static me.supcheg.advancedmanhunt.util.LocationParser.parseLocation;
import static me.supcheg.advancedmanhunt.util.LocationParser.serializeLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocationParserTest {

    World world;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        world = mock.addSimpleWorld("world");
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void parseThrows() {
        assertThrows(Throwable.class, () -> parseLocation("wgk2w-254];"));
    }

    @Test
    void parseSpawnWithoutDirection() {
        assertParseResult(ImmutableLocation.copyOf(world.getSpawnLocation()), "world[spawn]");
    }

    @Test
    void serializeSpawnWithoutDirection() {
        assertSerializeResult("world[spawn]", ImmutableLocation.copyOf(world.getSpawnLocation()));
    }

    @Test
    void parseSpawnWithDirection() {
        Location spawnWithDirection = world.getSpawnLocation();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        assertParseResult(ImmutableLocation.copyOf(spawnWithDirection), "world[spawn, 180, -25.5]");
    }

    @Test
    void serializeSpawnWithDirection() {
        Location spawnWithDirection = world.getSpawnLocation();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        assertSerializeResult("world[spawn, 180.0, -25.5]", ImmutableLocation.copyOf(spawnWithDirection));
    }

    @Test
    void parseLocationWithoutDirection() {
        assertParseResult(new ImmutableLocation(world, 20.5, .33, -30, 0, 0), "world[20.5, .33, -30]");
    }

    @Test
    void serializeLocationWithoutDirection() {
        assertSerializeResult("world[20.5, 0.33, -30.0]", new ImmutableLocation(world, 20.5, .33, -30, 0, 0));
    }

    @Test
    void parseLocationWithDirection() {
        assertParseResult(new ImmutableLocation(world, 10, 10, 10, .55f, -0.5f), "world[10, 10, 10, .55, -0.5]");
    }

    @Test
    void serializeLocationWithDirection() {
        assertSerializeResult("world[10.0, 10.0, 10.0, 0.55, -0.5]", new ImmutableLocation(world, 10, 10, 10, .55f, -0.5f));
    }

    private void assertParseResult(@NotNull ImmutableLocation expected, @NotNull String serialized) {
        assertEquals(expected, parseLocation(serialized));
    }

    private void assertSerializeResult(@NotNull String expected, @NotNull ImmutableLocation actual) {
        assertEquals(expected, serializeLocation(actual));
    }

}

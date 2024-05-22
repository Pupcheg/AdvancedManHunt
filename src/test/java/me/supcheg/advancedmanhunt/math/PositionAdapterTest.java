package me.supcheg.advancedmanhunt.math;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.builder.PositionBuilder;
import me.supcheg.advancedmanhunt.util.PositionAdapter;
import me.supcheg.advancedmanhunt.util.PositionAdapters;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static me.supcheg.advancedmanhunt.assertion.PositionAssertions.assertPositionsEquals;
import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionAdapterTest {

    World world;
    PositionAdapter<ImmutableLocation> adapter;

    @BeforeEach
    void setup() {
        ServerMock mock = MockBukkit.mock();
        world = mock.addSimpleWorld("world");
        adapter = PositionAdapters.immutableLocation();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void parseThrows() {
        assertThrows(Throwable.class, () -> adapter.deserialize("wgk2w-254];"));
    }

    @Test
    void parseSpawnWithoutDirection() throws IOException {
        assertParseResult(world.getSpawnLocation(), "world[spawn]");
    }

    @Test
    void serializeSpawnWithoutDirection() throws IOException {
        assertSerializeResult("world[spawn]", world.getSpawnLocation());
    }

    @Test
    void parseSpawnWithDirection() throws IOException {
        Location spawnWithDirection = world.getSpawnLocation();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        assertParseResult(spawnWithDirection, "world[spawn, 180, -25.5]");
    }

    @Test
    void serializeSpawnWithDirection() throws IOException {
        Location spawnWithDirection = world.getSpawnLocation();
        spawnWithDirection.setYaw(180f);
        spawnWithDirection.setPitch(-25.5f);

        assertSerializeResult("world[spawn, 180.0, -25.5]", position(spawnWithDirection));
    }

    @Test
    void parseLocationWithoutDirection() throws IOException {
        assertParseResult(PositionBuilder.position().world(world).xyz(20.5, .33, -30), "world[20.5, .33, -30]");
    }

    @Test
    void serializeLocationWithoutDirection() throws IOException {
        assertSerializeResult("world[20.5, 0.33, -30.0]", PositionBuilder.position().world(world).xyz(20.5, .33, -30));
    }

    @Test
    void parseLocationWithDirection() throws IOException {
        assertParseResult(
                PositionBuilder.position().world(world).xyz(10, 10, 10).yaw(.55f).pitch(-0.5f),
                "world[10, 10, 10, .55, -0.5]"
        );
    }

    @Test
    void serializeLocationWithDirection() throws IOException {
        assertSerializeResult(
                "world[10.0, 10.0, 10.0, 0.55, -0.5]",
                PositionBuilder.position().world(world).xyz(10, 10, 10).yaw(.55f).pitch(-0.5f).immutableLocation()
        );
    }

    private void assertParseResult(@NotNull Position expected, @NotNull String serialized) throws IOException {
        assertPositionsEquals(expected, adapter.deserialize(serialized));
    }

    private void assertSerializeResult(@NotNull String expected, @NotNull Position actual) throws IOException {
        assertEquals(expected, adapter.serialize(position(actual).immutableLocation()));
    }

}

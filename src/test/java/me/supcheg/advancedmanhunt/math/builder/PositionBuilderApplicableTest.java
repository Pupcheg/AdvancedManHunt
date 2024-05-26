package me.supcheg.advancedmanhunt.math.builder;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import be.seeseemelk.mockbukkit.MockBukkitInject;
import be.seeseemelk.mockbukkit.ServerMock;
import io.papermc.paper.math.Position;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.papermc.paper.math.Position.block;
import static io.papermc.paper.math.Position.fine;
import static me.supcheg.advancedmanhunt.math.builder.PositionBuilder.position;
import static me.supcheg.advancedmanhunt.math.builder.PositionBuilderApplicable.builderApplicable;
import static me.supcheg.advancedmanhunt.mock.WorldReferenceMock.mockWorldReference;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class PositionBuilderApplicableTest {

    @MockBukkitInject
    ServerMock mock;
    PositionBuilder builder;

    @BeforeEach
    void setup() {
        builder = position();
    }

    @Test
    void immutableLocationTest() {
        ImmutableLocation location = new ImmutableLocation(
                mockWorldReference(),
                10, 9, 8,
                7, 6
        );
        PositionBuilderApplicable applicable = builderApplicable(location);

        applicable.applyTo(builder);

        assertEquals(location.world(), builder.world());
        assertEquals(location.x(), builder.x());
        assertEquals(location.y(), builder.y());
        assertEquals(location.z(), builder.z());
        assertEquals(location.yaw(), builder.yaw());
        assertEquals(location.pitch(), builder.pitch());
    }

    @Test
    void bukkitLocationTest() {
        Location location = new Location(
                mockWorldReference().getWorld(),
                10, 9, 8,
                7, 6
        );
        PositionBuilderApplicable applicable = builderApplicable(location);

        applicable.applyTo(builder);

        assertEquals(WorldReference.of(location.getWorld()), builder.world());
        assertEquals(location.getX(), builder.x());
        assertEquals(location.getY(), builder.y());
        assertEquals(location.getZ(), builder.z());
        assertEquals(location.getYaw(), builder.yaw());
        assertEquals(location.getPitch(), builder.pitch());
    }

    @Test
    void positionBuilderTest() {
        PositionBuilder otherBuilder = position()
                .world(mockWorldReference())
                .xyz(10, 9, 8)
                .yaw(7)
                .pitch(6);
        PositionBuilderApplicable applicable = builderApplicable(otherBuilder);

        applicable.applyTo(builder);

        assertEquals(otherBuilder.world(), builder.world());
        assertEquals(otherBuilder.x(), builder.x());
        assertEquals(otherBuilder.y(), builder.y());
        assertEquals(otherBuilder.z(), builder.z());
        assertEquals(otherBuilder.yaw(), builder.yaw());
        assertEquals(otherBuilder.pitch(), builder.pitch());
    }

    @Test
    void finePositionTest() {
        Position position = fine(10, 9, 8);
        PositionBuilderApplicable applicable = builderApplicable(position);

        applicable.applyTo(builder);

        assertEquals(position.x(), builder.x());
        assertEquals(position.y(), builder.y());
        assertEquals(position.z(), builder.z());
    }

    @Test
    void blockPositionTest() {
        Position position = block(10, 9, 8);
        PositionBuilderApplicable applicable = builderApplicable(position);

        applicable.applyTo(builder);

        assertEquals(position.x(), builder.x());
        assertEquals(position.y(), builder.y());
        assertEquals(position.z(), builder.z());
    }
}

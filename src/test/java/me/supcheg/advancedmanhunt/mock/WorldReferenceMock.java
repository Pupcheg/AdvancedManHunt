package me.supcheg.advancedmanhunt.mock;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.supcheg.advancedmanhunt.region.WorldReference;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldReferenceMock {

    @NotNull
    public static WorldReference mockWorldReference() {
        return mockWorldReference("world");
    }

    @NotNull
    public static WorldReference mockWorldReference(@NotNull String name) {
        ServerMock mock = MockBukkit.getMock();
        if (mock == null) {
            throw new IllegalStateException("Not mocking");
        }

        World world = mock.getWorld(name);
        if (mock.getWorld(name) == null) {
            world = mock.addSimpleWorld(name);
        }

        return WorldReference.of(world);
    }
}

package me.supcheg.advancedmanhunt.region;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import be.seeseemelk.mockbukkit.MockBukkitInject;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.handler.RegionPortalHandler;
import me.supcheg.advancedmanhunt.math.ImmutableLocation;
import me.supcheg.advancedmanhunt.math.relative.RelativePositionSource;
import me.supcheg.advancedmanhunt.mock.MockBukkitUtilExtension;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.UUID;

import static me.supcheg.advancedmanhunt.assertion.PositionAssertions.assertIncludes;
import static me.supcheg.advancedmanhunt.assertion.PositionAssertions.assertPositionsEquals;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.mock.WorldReferenceMock.mockWorldReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({MockBukkitExtension.class, MockBukkitUtilExtension.class})
class RegionPortalHandlerTest {

    @MockBukkitInject
    ServerMock mock;

    GameRegion overworld;
    GameRegion nether;
    GameRegion end;

    RelativePositionSource overworldSource;
    RelativePositionSource netherSource;
    RelativePositionSource endSource;

    ImmutableLocation spawnLocation;

    @BeforeEach
    void setup() {
        GameRegionRepository regionRepository = new DefaultGameRegionRepository();

        ManHuntGame game = new ManHuntGame(UUID.randomUUID(), UUID.randomUUID());

        overworld = regionRepository.getRegion(RealEnvironment.OVERWORLD);
        overworldSource = overworld.positionSource();
        game.setOverworld(overworld);
        nether = regionRepository.getRegion(RealEnvironment.NETHER);
        netherSource = nether.positionSource();
        game.setNether(nether);
        end = regionRepository.getRegion(RealEnvironment.THE_END);
        endSource = end.positionSource();
        game.setEnd(end);

        spawnLocation = overworldSource
                .relative(0, 60, 0)
                .immutableRelative();
        game.setSpawnLocation(spawnLocation);

        game.registerHandler(RegionPortalHandler::new);
    }

    @Test
    void playerOverworldToNetherTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworldSource.relative(1, 0, 1).bukkitAbsolute(),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertIncludes(netherSource.box(), event.getTo());
    }

    @Test
    void playerOverworldToNetherBorderExitTest() {
        double originalNetherMultiplier = config().game.portal.netherMultiplier;
        config().game.portal.netherMultiplier = 1 / originalNetherMultiplier;

        PlayerPortalEvent event = executePlayerTeleport(
                overworldSource.absolute(overworld.end()).bukkitAbsolute(),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertIncludes(netherSource.box(), event.getTo());

        config().game.portal.netherMultiplier = originalNetherMultiplier;
    }

    @Test
    void playerNetherToOverworldBorderExitTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                netherSource.absolute(nether.end()).bukkitAbsolute(),
                unexpectedLocationIn(overworld),
                PortalType.NETHER
        );

        assertIncludes(overworldSource.box(), event.getTo());
    }

    @Test
    void playerOverworldToEndTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworldSource.absolute(overworldSource.offset()).bukkitAbsolute(),
                unexpectedLocationIn(end),
                PortalType.ENDER
        );

        assertEquals(
                endSource.relative(100.5, 49, 0.5).bukkitAbsolute(),
                event.getTo()
        );
    }

    @Disabled("""
            Receiver class be.seeseemelk.mockbukkit.entity.PlayerMock
            does not define or inherit an implementation
            of the resolved method 'abstract org.bukkit.Location getRespawnLocation()'
            of interface org.bukkit.entity.Player
            """)
    @Test
    void playerEndToOverworldTest() {
        Player player = mock.addPlayer();
        player.setRespawnLocation(new Location(mockWorldReference().getWorld(), 0, 0, 0), true);

        PlayerPortalEvent event = executePlayerTeleport(player,
                endSource.absolute(endSource.offset()).bukkitAbsolute(),
                unexpectedLocationIn(overworld),
                PortalType.ENDER
        );

        assertEquals(player.getRespawnLocation(), event.getTo());
    }


    @Test
    void entityOverworldToNetherTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworldSource
                        .absolute(overworldSource.offset())
                        .bukkitAbsolute()
                        .add(1, 0, 1),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertIncludes(netherSource.box(), event.getTo());
    }

    @Test
    void entityOverworldToNetherBorderExitTest() {
        config().game.portal.netherMultiplier = 1 / 8d;

        EntityTeleportEvent event = executeEntityTeleport(
                overworldSource.absolute(overworld.end()).bukkitAbsolute(),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertIncludes(netherSource.box(), event.getTo());

        config().game.portal.netherMultiplier = 8d;
    }

    @Test
    void entityNetherToOverworldBorderExitTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                netherSource.absolute(nether.end()).bukkitAbsolute(),
                unexpectedLocationIn(overworld),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertIncludes(overworldSource.box(), event.getTo());
    }

    @Test
    void entityOverworldToEndTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworldSource.absolute(overworldSource.offset()).bukkitAbsolute(),
                unexpectedLocationIn(end),
                PortalType.ENDER
        );

        assertNotNull(event.getTo());
        assertEquals(
                endSource.relative(100.5, 49, 0.5).bukkitAbsolute(),
                event.getTo()
        );
    }

    @Test
    void entityEndToOverworldTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                endSource.absolute(endSource.offset()).bukkitAbsolute(),
                unexpectedLocationIn(overworld),
                PortalType.ENDER
        );

        assertPositionsEquals(spawnLocation, event.getTo());
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private EntityPortalEvent executeEntityTeleport(@NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PortalType portalType) {
        return executeEntityTeleport(fromLocation.getWorld().spawn(fromLocation, Pig.class), fromLocation, toLocation, portalType);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private EntityPortalEvent executeEntityTeleport(@NotNull Entity entity,
                                                    @NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PortalType portalType) {
        EntityPortalEvent event = new EntityPortalEvent(entity, fromLocation, toLocation, 128, portalType);
        event.callEvent();
        assertNotEquals(toLocation, event.getTo());
        return event;
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private PlayerPortalEvent executePlayerTeleport(@NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PortalType portalType) {
        return executePlayerTeleport(mock.addPlayer(), fromLocation, toLocation, portalType);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private PlayerPortalEvent executePlayerTeleport(@NotNull Player player,
                                                    @NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PortalType portalType) {
        PlayerPortalEvent event = new PlayerPortalEvent(player, fromLocation, toLocation, asCause(portalType));
        event.callEvent();
        assertNotEquals(toLocation, event.getTo());
        return event;
    }

    @NotNull
    @Contract(pure = true)
    private static PlayerTeleportEvent.TeleportCause asCause(@NotNull PortalType portalType) {
        return switch (portalType) {
            case NETHER -> PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
            case ENDER -> PlayerTeleportEvent.TeleportCause.END_PORTAL;
            case CUSTOM -> PlayerTeleportEvent.TeleportCause.UNKNOWN;
        };
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private Location unexpectedLocationIn(@NotNull GameRegion gameRegion) {
        return new UnexpectedLocation(gameRegion.positionSource().world().getWorld());
    }

    private static final class UnexpectedLocation extends Location {

        private UnexpectedLocation(@NotNull World world) {
            super(world, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
        }

        @Override
        @NotNull
        @Contract(pure = true)
        public String toString() {
            return "UnexpectedLocation";
        }

        @Override
        public int hashCode() {
            return getWorld().hashCode();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof UnexpectedLocation o && Objects.equals(o.getWorld(), getWorld());
        }
    }
}

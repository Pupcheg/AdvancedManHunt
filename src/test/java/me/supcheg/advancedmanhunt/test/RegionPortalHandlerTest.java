package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.assertion.KeyedCoordAssertions.assertInBoundInclusive;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Game.Portal.NETHER_MULTIPLIER;
import static me.supcheg.advancedmanhunt.coord.KeyedCoord.asKeyedCoord;
import static org.junit.jupiter.api.Assertions.*;

class RegionPortalHandlerTest {

    private ServerMock mock;

    private GameRegion overworldRegion;
    private GameRegion netherRegion;
    private GameRegion endRegion;

    private Location spawnLocation;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();

        GameRegionRepository regionRepository = new DefaultGameRegionRepository(
                new DummyContainerAdapter(),
                new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin())
        );

        overworldRegion = regionRepository.getRegion(World.Environment.NORMAL);
        netherRegion = regionRepository.getRegion(World.Environment.NETHER);
        endRegion = regionRepository.getRegion(World.Environment.THE_END);

        spawnLocation = overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld(), 60);

        RegionPortalHandler handler = new RegionPortalHandler(
                regionRepository,
                overworldRegion, netherRegion, endRegion,
                spawnLocation
        );
        new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin()).addListener(handler);
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void playerOverworldToNetherTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()).add(1, 0, 1),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());
    }

    @Test
    void playerOverworldToNetherBorderExitTest() {
        double originalNetherMultiplier = NETHER_MULTIPLIER;
        NETHER_MULTIPLIER = 1 / originalNetherMultiplier;

        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());

        NETHER_MULTIPLIER = originalNetherMultiplier;
    }

    @Test
    void playerNetherToOverworldBorderExitTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                netherRegion.getEndBlock().asLocation(netherRegion.getWorld()),
                unexpectedLocationIn(overworldRegion),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), overworldRegion.getStartBlock(), overworldRegion.getEndBlock());
    }

    @Test
    void playerOverworldToEndTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(endRegion),
                PortalType.ENDER
        );

        assertEquals(
                endRegion.addDelta(new Location(endRegion.getWorld(), 100.5, 49, 0.5)),
                event.getTo()
        );
    }

    @Test
    void playerEndToOverworldTest() {
        Player player = mock.addPlayer();
        player.setBedSpawnLocation(new Location(mock.addSimpleWorld("world"), 0, 0, 0), true);

        PlayerPortalEvent event = executePlayerTeleport(player,
                endRegion.getCenterBlock().asLocation(endRegion.getWorld()),
                unexpectedLocationIn(overworldRegion),
                PortalType.ENDER
        );

        assertEquals(player.getBedSpawnLocation(), event.getTo());
    }


    @Test
    void entityOverworldToNetherTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()).add(1, 0, 1),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());
    }

    @Test
    void entityOverworldToNetherBorderExitTest() {
        NETHER_MULTIPLIER = 1 / 8d;

        EntityTeleportEvent event = executeEntityTeleport(
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());

        NETHER_MULTIPLIER = 8d;
    }

    @Test
    void entityNetherToOverworldBorderExitTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                netherRegion.getEndBlock().asLocation(netherRegion.getWorld()),
                unexpectedLocationIn(overworldRegion),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), overworldRegion.getStartBlock(), overworldRegion.getEndBlock());
    }

    @Test
    void entityOverworldToEndTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(endRegion),
                PortalType.ENDER
        );

        assertNotNull(event.getTo());
        assertEquals(
                endRegion.addDelta(new Location(endRegion.getWorld(), 100.5, 49, 0.5)),
                event.getTo()
        );
    }

    @Test
    void entityEndToOverworldTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                endRegion.getCenterBlock().asLocation(endRegion.getWorld()),
                unexpectedLocationIn(overworldRegion),
                PortalType.ENDER
        );

        assertEquals(spawnLocation, event.getTo());
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
        return new UnexpectedLocation(gameRegion.getWorld());
    }

    private static final class UnexpectedLocation extends ImmutableLocation {

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

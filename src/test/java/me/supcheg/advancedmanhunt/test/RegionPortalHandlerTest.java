package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static me.supcheg.advancedmanhunt.assertion.KeyedCoordAssertions.assertInBoundInclusive;
import static me.supcheg.advancedmanhunt.coord.KeyedCoord.asKeyedCoord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegionPortalHandlerTest {

    private ServerMock mock;

    private GameRegion overworldRegion;
    private GameRegion netherRegion;
    private GameRegion endRegion;

    private ImmutableLocation spawnLocation;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();

        GameRegionRepository regionRepository = new DefaultGameRegionRepository(
                new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin())
        );

        overworldRegion = regionRepository.getRegion(World.Environment.NORMAL);
        netherRegion = regionRepository.getRegion(World.Environment.NETHER);
        endRegion = regionRepository.getRegion(World.Environment.THE_END);

        spawnLocation = ImmutableLocation.copyOf(overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld(), 60));

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
        double originalNetherMultiplier = AdvancedManHuntConfig.get().game.portal.netherMultiplier;
        AdvancedManHuntConfig.get().game.portal.netherMultiplier = 1 / originalNetherMultiplier;

        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());

        AdvancedManHuntConfig.get().game.portal.netherMultiplier = originalNetherMultiplier;
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

    @Disabled("""
            Receiver class be.seeseemelk.mockbukkit.entity.PlayerMock
            does not define or inherit an implementation
            of the resolved method 'abstract org.bukkit.Location getRespawnLocation()'
            of interface org.bukkit.entity.Player
            """)
    @Test
    void playerEndToOverworldTest() {
        Player player = mock.addPlayer();
        player.setRespawnLocation(new Location(mock.addSimpleWorld("world"), 0, 0, 0), true);

        PlayerPortalEvent event = executePlayerTeleport(player,
                endRegion.getCenterBlock().asLocation(endRegion.getWorld()),
                unexpectedLocationIn(overworldRegion),
                PortalType.ENDER
        );

        assertEquals(player.getRespawnLocation(), event.getTo());
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
        AdvancedManHuntConfig.get().game.portal.netherMultiplier = 1 / 8d;

        EntityTeleportEvent event = executeEntityTeleport(
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                unexpectedLocationIn(netherRegion),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());

        AdvancedManHuntConfig.get().game.portal.netherMultiplier = 8d;
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

        assertEquals(spawnLocation, ImmutableLocation.copyOf(event.getTo()));
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

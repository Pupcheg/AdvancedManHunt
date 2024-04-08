package me.supcheg.advancedmanhunt.region;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.game.ManHuntGame;
import me.supcheg.advancedmanhunt.game.handler.RegionPortalHandler;
import me.supcheg.advancedmanhunt.paper.BukkitUtilMock;
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
import java.util.UUID;

import static me.supcheg.advancedmanhunt.assertion.KeyedCoordAssertions.assertInBoundInclusive;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.coord.Coord.asKeyedCoord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegionPortalHandlerTest {

    private ServerMock mock;

    private GameRegion overworld;
    private GameRegion nether;
    private GameRegion end;

    private ImmutableLocation spawnLocation;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();
        BukkitUtilMock.mock();

        GameRegionRepository regionRepository = new DefaultGameRegionRepository();

        ManHuntGame game = new ManHuntGame(UUID.randomUUID(), UUID.randomUUID());

        overworld = regionRepository.getRegion(RealEnvironment.OVERWORLD);
        game.setOverworld(overworld);
        nether = regionRepository.getRegion(RealEnvironment.NETHER);
        game.setNether(nether);
        end = regionRepository.getRegion(RealEnvironment.THE_END);
        game.setEnd(end);

        spawnLocation = ImmutableLocation.immutableCopy(overworld.getCenterBlock().asLocation(overworld.getWorld(), 60));
        game.setSpawnLocation(spawnLocation);

        game.registerHandler(RegionPortalHandler::new);
    }

    @AfterEach
    void shutdown() {
        BukkitUtilMock.unmock();
        MockBukkit.unmock();
    }

    @Test
    void playerOverworldToNetherTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworld.getCenterBlock().asLocation(overworld.getWorld()).add(1, 0, 1),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), nether.getStartBlock(), nether.getEndBlock());
    }

    @Test
    void playerOverworldToNetherBorderExitTest() {
        double originalNetherMultiplier = config().game.portal.netherMultiplier;
        config().game.portal.netherMultiplier = 1 / originalNetherMultiplier;

        PlayerPortalEvent event = executePlayerTeleport(
                overworld.getEndBlock().asLocation(overworld.getWorld()),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), nether.getStartBlock(), nether.getEndBlock());

        config().game.portal.netherMultiplier = originalNetherMultiplier;
    }

    @Test
    void playerNetherToOverworldBorderExitTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                nether.getEndBlock().asLocation(nether.getWorld()),
                unexpectedLocationIn(overworld),
                PortalType.NETHER
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), overworld.getStartBlock(), overworld.getEndBlock());
    }

    @Test
    void playerOverworldToEndTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworld.getCenterBlock().asLocation(overworld.getWorld()),
                unexpectedLocationIn(end),
                PortalType.ENDER
        );

        assertEquals(
                end.addDelta(new Location(end.getWorld(), 100.5, 49, 0.5)),
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
                end.getCenterBlock().asLocation(end.getWorld()),
                unexpectedLocationIn(overworld),
                PortalType.ENDER
        );

        assertEquals(player.getRespawnLocation(), event.getTo());
    }


    @Test
    void entityOverworldToNetherTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworld.getCenterBlock().asLocation(overworld.getWorld()).add(1, 0, 1),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), nether.getStartBlock(), nether.getEndBlock());
    }

    @Test
    void entityOverworldToNetherBorderExitTest() {
        config().game.portal.netherMultiplier = 1 / 8d;

        EntityTeleportEvent event = executeEntityTeleport(
                overworld.getEndBlock().asLocation(overworld.getWorld()),
                unexpectedLocationIn(nether),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), nether.getStartBlock(), nether.getEndBlock());

        config().game.portal.netherMultiplier = 8d;
    }

    @Test
    void entityNetherToOverworldBorderExitTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                nether.getEndBlock().asLocation(nether.getWorld()),
                unexpectedLocationIn(overworld),
                PortalType.NETHER
        );

        assertNotNull(event.getTo());
        assertInBoundInclusive(asKeyedCoord(event.getTo()), overworld.getStartBlock(), overworld.getEndBlock());
    }

    @Test
    void entityOverworldToEndTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                overworld.getCenterBlock().asLocation(overworld.getWorld()),
                unexpectedLocationIn(end),
                PortalType.ENDER
        );

        assertNotNull(event.getTo());
        assertEquals(
                end.addDelta(new Location(end.getWorld(), 100.5, 49, 0.5)),
                event.getTo()
        );
    }

    @Test
    void entityEndToOverworldTest() {
        EntityTeleportEvent event = executeEntityTeleport(
                end.getCenterBlock().asLocation(end.getWorld()),
                unexpectedLocationIn(overworld),
                PortalType.ENDER
        );

        assertEquals(spawnLocation, ImmutableLocation.immutableCopy(event.getTo()));
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

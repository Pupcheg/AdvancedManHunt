package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.RegionPortalHandler;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.structure.DummyEventListenerRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static me.supcheg.advancedmanhunt.assertion.KeyedCoordAssertions.assertInBoundInclusive;
import static me.supcheg.advancedmanhunt.coord.KeyedCoord.asKeyedCoord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RegionPortalHandlerTest {

    private ServerMock mock;

    private GameRegion overworldRegion;
    private GameRegion netherRegion;
    private GameRegion endRegion;

    @BeforeEach
    void setup() {
        mock = MockBukkit.mock();

        GameRegionRepository regionRepository = new DefaultGameRegionRepository(
                new DummyContainerAdapter(),
                JsonSerializer.createGson(),
                new DummyEventListenerRegistry()
        );

        overworldRegion = regionRepository.getRegion(World.Environment.NORMAL);
        netherRegion = regionRepository.getRegion(World.Environment.NETHER);
        endRegion = regionRepository.getRegion(World.Environment.THE_END);

        RegionPortalHandler handler = new RegionPortalHandler(
                regionRepository,
                overworldRegion, netherRegion, endRegion,
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld(), 60)
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
                netherRegion.getCenterBlock().asLocation(netherRegion.getWorld()),
                PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());
    }

    @Test
    void playerOverworldToNetherOverflowTest() {
        AdvancedManHuntConfig.Game.Portal.NETHER_MULTIPLIER = 1 / 8d;

        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                netherRegion.getEndBlock().asLocation(netherRegion.getWorld()),
                PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), netherRegion.getStartBlock(), netherRegion.getEndBlock());

        AdvancedManHuntConfig.Game.Portal.NETHER_MULTIPLIER = 8d;
    }

    @Test
    void playerNetherToOverworldOverflowTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                netherRegion.getEndBlock().asLocation(netherRegion.getWorld()),
                overworldRegion.getEndBlock().asLocation(overworldRegion.getWorld()),
                PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
        );

        assertInBoundInclusive(asKeyedCoord(event.getTo()), overworldRegion.getStartBlock(), overworldRegion.getEndBlock());
    }

    @Test
    void playerOverworldToEndTest() {
        PlayerPortalEvent event = executePlayerTeleport(
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()),
                endRegion.getCenterBlock().asLocation(endRegion.getWorld()),
                PlayerTeleportEvent.TeleportCause.END_PORTAL
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
                overworldRegion.getCenterBlock().asLocation(overworldRegion.getWorld()),
                PlayerTeleportEvent.TeleportCause.END_PORTAL
        );

        assertEquals(player.getBedSpawnLocation(), event.getTo());
    }

    @NotNull
    @Contract("_, _, _ -> new")
    private PlayerPortalEvent executePlayerTeleport(@NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PlayerTeleportEvent.TeleportCause cause) {
        return executePlayerTeleport(mock.addPlayer(), fromLocation, toLocation, cause);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private PlayerPortalEvent executePlayerTeleport(@NotNull Player player,
                                                    @NotNull Location fromLocation,
                                                    @NotNull Location toLocation,
                                                    @NotNull PlayerTeleportEvent.TeleportCause cause) {
        PlayerPortalEvent event = new PlayerPortalEvent(player, fromLocation, toLocation, cause);
        event.callEvent();
        assertNotEquals(toLocation, event.getTo());
        return event;
    }
}

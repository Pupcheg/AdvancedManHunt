package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.test.argument.EnvironmentArgumentsProvider;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;

import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;
import static org.junit.jupiter.api.Assertions.*;

class GameRegionRepositoryTest {
    private GameRegionRepository regionRepository;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        regionRepository = TestPaperPlugin.load().getGameRegionRepository();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void customEnvironmentTest() {
        assertThrows(Exception.class, () -> regionRepository.getRegion(Environment.CUSTOM));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void fastSameTest(@NotNull Environment environment,
                      @NotNull Environment @NotNull [] anotherEnvironments) {
        GameRegion region = regionRepository.getRegion(environment);

        assertSame(region, regionRepository.getRegion(environment));
        for (Environment another : anotherEnvironments) {
            assertNotSame(region, regionRepository.getRegion(another));
        }
        region.setReserved(true);

        assertNotSame(region, regionRepository.getRegion(environment));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void firstRegionTest(@NotNull Environment environment) {
        GameRegion region = regionRepository.getRegion(environment);

        assertEquals(0, region.getStartRegion().getKey());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getX());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getZ());
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void fewRegionTest(@NotNull Environment environment) {
        int regionsCount = 32;

        List<GameRegion> regionList = new ArrayList<>(regionsCount);

        for (int i = 0; i < regionsCount; i++) {
            GameRegion region = regionRepository.getAndReserveRegion(environment);
            assertFalse(regionList.contains(region), "Region: " + region);

            World world = region.getWorld();

            int startChunkX = region.getStartChunk().getX();
            int startChunkZ = region.getStartChunk().getZ();
            Location firstBlockLocation = world.getChunkAt(startChunkX, startChunkZ)
                    .getBlock(0, 0, 0)
                    .getLocation();

            assertEquals(firstBlockLocation, region.addDelta(new Location(world, 0, 0, 0)),
                    "Region: " + region);

            int endChunkX = region.getEndChunk().getX();
            int endChunkZ = region.getEndChunk().getZ();
            Location lastBlockLocation = world.getChunkAt(endChunkX, endChunkZ)
                    .getBlock(15, 0, 15)
                    .getLocation();

            int xz = (MAX_REGION_SIDE_SIZE.getRegions() * 32 + 31) * 16 + 15;
            assertEquals(lastBlockLocation, region.addDelta(new Location(world, xz, 0, xz)),
                    "Region: " + region);

            regionList.add(region);
        }

        regionList.forEach(region -> region.setReserved(false));

        for (int i = 0; i < regionsCount; i++) {
            assertTrue(regionList.contains(regionRepository.getAndReserveRegion(environment)));
        }

        assertFalse(regionList.contains(regionRepository.getAndReserveRegion(environment)));
    }

}

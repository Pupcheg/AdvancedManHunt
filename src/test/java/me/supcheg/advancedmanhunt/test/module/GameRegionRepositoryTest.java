package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.exception.RepositoryOverflowException;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.test.argument.EnvironmentArgumentsProvider;
import me.supcheg.advancedmanhunt.test.structure.InjectingPaperPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_REGIONS_PER_WORLD;
import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.Region.MAX_WORLDS_PER_ENVIRONMENT;
import static me.supcheg.advancedmanhunt.region.RegionConstants.REGION_SIDE_SIZE;

class GameRegionRepositoryTest {
    private int maxRegions;
    private GameRegionRepository regionRepository;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        maxRegions = MAX_REGIONS_PER_WORLD * MAX_WORLDS_PER_ENVIRONMENT;
        regionRepository = InjectingPaperPlugin.load().getGameRegionRepository();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void customEnvironmentTest() {
        Assertions.assertThrows(Exception.class, () -> regionRepository.getRegion(Environment.CUSTOM));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void fastSameTest(@NotNull Environment environment,
                      @NotNull Environment @NotNull [] anotherEnvironments) {
        GameRegion region = regionRepository.getRegion(environment);

        Assertions.assertSame(region, regionRepository.getRegion(environment));
        for (Environment another : anotherEnvironments) {
            Assertions.assertNotSame(region, regionRepository.getRegion(another));
        }
        region.setReserved(true);

        Assertions.assertNotSame(region, regionRepository.getRegion(environment));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void firstRegionTest(@NotNull Environment environment) {
        GameRegion region = regionRepository.getRegion(environment);

        Assertions.assertEquals(0, region.getStartRegion().getKey());
        Assertions.assertEquals(REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getX());
        Assertions.assertEquals(REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getZ());
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void fewRegionTest(@NotNull Environment environment) {
        int regionsCount = maxRegions - 1;

        List<GameRegion> regionList = new ArrayList<>(regionsCount);

        for (int i = 0; i < regionsCount; i++) {
            GameRegion region = regionRepository.getAndReserveRegion(environment);
            Assertions.assertFalse(regionList.contains(region), "Region: " + region);

            World world = region.getWorld();

            Location firstBlockLocation = world.getChunkAt(region.getStartChunk().getKey())
                    .getBlock(0, 0, 0)
                    .getLocation();

            Assertions.assertEquals(firstBlockLocation, region.addDelta(new Location(world, 0, 0, 0)),
                    "Region: " + region);

            Location lastBlockLocation = world.getChunkAt(region.getEndChunk().getKey())
                    .getBlock(15, 0, 15)
                    .getLocation();

            int xz = (REGION_SIDE_SIZE.getRegions() * 32 + 31) * 16 + 15;
            Assertions.assertEquals(lastBlockLocation, region.addDelta(new Location(world, xz, 0, xz)),
                    "Region: " + region);

            regionList.add(region);
        }

        regionList.forEach(region -> region.setReserved(false));

        for (int i = 0; i < regionsCount; i++) {
            Assertions.assertTrue(regionList.contains(regionRepository.getAndReserveRegion(environment)));
        }

        Assertions.assertFalse(regionList.contains(regionRepository.getAndReserveRegion(environment)));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void overflowTest(@NotNull Environment environment) {
        for (int i = 0; i < maxRegions; i++) {
            regionRepository.getAndReserveRegion(environment);
        }
        Assertions.assertThrows(RepositoryOverflowException.class, () -> regionRepository.getRegion(environment));
    }

}

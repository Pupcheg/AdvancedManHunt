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
    void customEnvironmentThrowTest() {
        assertThrows(Exception.class, () -> regionRepository.getRegion(Environment.CUSTOM));
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void notSameRegionsInNotSameEnvironmentsTest(@NotNull Environment environment,
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
    void sideSizeTest(@NotNull Environment environment) {
        GameRegion region = regionRepository.getRegion(environment);

        assertEquals(0, region.getStartRegion().getX());
        assertEquals(0, region.getStartRegion().getZ());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getX());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.getEndRegion().getZ());
    }

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void validDeltaTest(@NotNull Environment environment) {
        GameRegion region = regionRepository.getRegion(environment);
        World world = region.getWorld();
        Location centerLocation = region.getCenterBlock().asLocation(world);

        assertEquals(centerLocation, region.addDelta(new Location(world, 0, 0, 0)));
    }

}

package me.supcheg.advancedmanhunt.region;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.argument.RealEnvironmentArgumentsProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_RADIUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class GameRegionRepositoryTest {
    private GameRegionRepository regionRepository;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        regionRepository = new DefaultGameRegionRepository();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @ArgumentsSource(RealEnvironmentArgumentsProvider.class)
    void notSameRegionsInNotSameEnvironmentsTest(@NotNull RealEnvironment environment,
                                                 @NotNull RealEnvironment @NotNull [] anotherEnvironments) {
        GameRegion region = regionRepository.getRegion(environment);

        assertSame(region, regionRepository.getRegion(environment));
        for (RealEnvironment another : anotherEnvironments) {
            assertNotSame(region, regionRepository.getRegion(another));
        }
        region.setReserved(true);

        assertNotSame(region, regionRepository.getRegion(environment));
    }

    @ParameterizedTest
    @ArgumentsSource(RealEnvironmentArgumentsProvider.class)
    void sideSizeTest(@NotNull RealEnvironment environment) {
        GameRegion region = regionRepository.getRegion(environment);

        assertEquals(0, region.getStartRegion().getX());
        assertEquals(0, region.getStartRegion().getZ());
        assertEquals(MAX_REGION_RADIUS.getRegions() * 2, region.getEndRegion().getX());
        assertEquals(MAX_REGION_RADIUS.getRegions() * 2, region.getEndRegion().getZ());
    }

    @ParameterizedTest
    @ArgumentsSource(RealEnvironmentArgumentsProvider.class)
    void validDeltaTest(@NotNull RealEnvironment environment) {
        GameRegion region = regionRepository.getRegion(environment);
        World world = region.getWorld();
        Location centerLocation = region.getCenterBlock().asLocation(world);

        assertEquals(centerLocation, region.addDelta(new Location(world, 0, 0, 0)));
    }

    @ParameterizedTest
    @ArgumentsSource(RealEnvironmentArgumentsProvider.class)
    void findRegionTest(@NotNull RealEnvironment environment) {
        for (int i = 0; i < config().region.maxRegionsPerWorld; i++) {
            GameRegion region = regionRepository.getAndReserveRegion(environment);
            World world = region.getWorld();

            assertEquals(region, regionRepository.findRegion(region.getStartBlock().asLocation(world)));
            assertEquals(region, regionRepository.findRegion(region.getCenterBlock().asLocation(world)));
            assertEquals(region, regionRepository.findRegion(region.getEndBlock().asLocation(world)));
        }
    }

}

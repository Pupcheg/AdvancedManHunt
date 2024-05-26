package me.supcheg.advancedmanhunt.region;

import be.seeseemelk.mockbukkit.MockBukkitExtension;
import me.supcheg.advancedmanhunt.math.relative.RelativePositionSource;
import me.supcheg.advancedmanhunt.mock.MockBukkitUtilExtension;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.argument.RealEnvironmentArgumentsProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig.config;
import static me.supcheg.advancedmanhunt.region.GameRegionRepository.MAX_REGION_SIDE_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith({MockBukkitExtension.class, MockBukkitUtilExtension.class})
class GameRegionRepositoryTest {
    GameRegionRepository regionRepository;

    @BeforeEach
    void setup() {
        regionRepository = new DefaultGameRegionRepository();
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

        assertEquals(0, region.start().getRegionX());
        assertEquals(0, region.end().getRegionZ());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.end().getRegionX());
        assertEquals(MAX_REGION_SIDE_SIZE.getRegions(), region.end().getRegionZ());
    }

    @ParameterizedTest
    @ArgumentsSource(RealEnvironmentArgumentsProvider.class)
    void findRegionTest(@NotNull RealEnvironment environment) {
        for (int i = 0; i < config().region.maxRegionsPerWorld; i++) {
            GameRegion region = regionRepository.getAndReserveRegion(environment);
            RelativePositionSource positionSource = region.positionSource();

            assertEquals(region, regionRepository.findRegion(positionSource.absolute(region.start()).bukkitAbsolute()));
            assertEquals(region, regionRepository.findRegion(positionSource.absolute(positionSource.offset()).bukkitAbsolute()));
            assertEquals(region, regionRepository.findRegion(positionSource.absolute(region.end()).bukkitAbsolute()));
        }
    }

}

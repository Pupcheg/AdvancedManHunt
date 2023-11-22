package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.event.impl.PluginBasedEventListenerRegistry;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.GameRegionRepository;
import me.supcheg.advancedmanhunt.region.impl.DefaultGameRegionRepository;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import me.supcheg.advancedmanhunt.structure.argument.EnvironmentArgumentsProvider;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameRegionRepositoryTest {
    private GameRegionRepository regionRepository;

    @BeforeEach
    void setup() {
        MockBukkit.mock();
        regionRepository = new DefaultGameRegionRepository(
                new DummyContainerAdapter(),
                new PluginBasedEventListenerRegistry(MockBukkit.createMockPlugin())
        );
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

    @ParameterizedTest
    @ArgumentsSource(EnvironmentArgumentsProvider.class)
    void findRegionTest(@NotNull Environment environment) {
        for (int i = 0; i < AdvancedManHuntConfig.Region.MAX_REGIONS_PER_WORLD; i++) {
            GameRegion region = regionRepository.getAndReserveRegion(environment);
            World world = region.getWorld();

            assertEquals(region, regionRepository.findRegion(region.getStartBlock().asLocation(world)));
            assertEquals(region, regionRepository.findRegion(region.getCenterBlock().asLocation(world)));
            assertEquals(region, regionRepository.findRegion(region.getEndBlock().asLocation(world)));
        }
    }

}

package me.supcheg.advancedmanhunt.config;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.io.ContainerAdapter;
import net.kyori.adventure.sound.Sound;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.spongepowered.configurate.objectmapping.meta.PostProcess;

import java.nio.file.Path;
import java.time.Duration;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@SuppressWarnings("CanBeFinal")
public class ConfigLoaderTest {

    private static World world;
    private static TestConfig config;

    @SneakyThrows
    @BeforeAll
    static void setup() {
        ServerMock mock = MockBukkit.mock();

        world = mock.addSimpleWorld("world");

        ContainerAdapter containerAdapter = Mockito.mock(ContainerAdapter.class);
        Mockito.when(containerAdapter.resolveData(anyString()))
                .then(inv -> Path.of("build", "resources", "test", inv.getArgument(0)));

        ConfigLoader configLoader = new ConfigLoader(containerAdapter);

        configLoader.load("config_loader_test.yml", TestConfig.class);

        config = TestConfig.get();
    }

    @AfterAll
    static void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void replaceValueTest() {
        assertEquals("replaced", config.string1);
    }

    @Test
    void stringTest() {
        assertEquals("string", config.string2);
    }

    @Test
    void adventureSoundTest() {
        assertEquals(sound()
                        .type(key("sound_1_key"))
                        .source(Sound.Source.BLOCK)
                        .volume(10)
                        .pitch(2)
                        .build(),
                config.sound1
        );

        assertEquals(sound()
                        .type(key("sound_2_key"))
                        .source(Sound.Source.MASTER)
                        .volume(1)
                        .pitch(1)
                        .build(),
                config.sound2
        );
    }

    @Test
    void durationTest() {
        assertEquals(Duration.ofSeconds(10), config.duration1);
        assertEquals(Duration.ofMinutes(60), config.duration2);
        assertEquals(Duration.ofHours(40), config.duration3);
        assertEquals(Duration.ofDays(365), config.duration4);
    }

    @Test
    void distanceTest() {
        assertEquals(Distance.ofBlocks(8), config.distance1);
        assertEquals(Distance.ofChunks(20), config.distance2);
        assertEquals(Distance.ofRegions(100), config.distance3);
    }

    @Test
    void locationTest() {
        assertEquals(ImmutableLocation.immutableCopy(world.getSpawnLocation()), config.location);
    }

    public static class TestConfig implements ConfigurationPart {

        private static TestConfig INSTANCE;

        public static TestConfig get() {
            return INSTANCE;
        }

        @PostProcess
        public void postProcess() {
            INSTANCE = this;
        }

        public String string1 = "default";
        public String string2;

        public Sound sound1;
        public Sound sound2;

        public Duration duration1;
        public Duration duration2;
        public Duration duration3;
        public Duration duration4;

        public Distance distance1;
        public Distance distance2;
        public Distance distance3;

        public ImmutableLocation location;
    }
}

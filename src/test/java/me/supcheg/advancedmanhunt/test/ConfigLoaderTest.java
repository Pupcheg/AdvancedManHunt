package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.util.ContainerAdapter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SuppressWarnings("CanBeFinal")
public class ConfigLoaderTest {

    private static World world;

    @SneakyThrows
    @BeforeAll
    static void setup() {
        ServerMock mock = MockBukkit.mock();

        world = mock.addSimpleWorld("world");

        ContainerAdapter containerAdapter = Mockito.mock(ContainerAdapter.class);
        Mockito.when(containerAdapter.unpackResource(any()))
                .then(inv -> Path.of("build", "resources", "test", inv.getArgument(0)));

        ConfigLoader configLoader = new ConfigLoader(containerAdapter);

        configLoader.load("config_loader_test.yml", ConfigLoaderTest.class);
    }

    @AfterAll
    static void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void replaceValueTest() {
        assertEquals("replaced", STRING_1);
    }

    @Test
    void stringTest() {
        assertEquals("string", STRING_2);
    }

    @Test
    void multilineStringTest() {
        assertEquals("first\nsecond\nthird", STRING_3);
    }

    @Test
    void adventureComponentTest() {
        assertEquals(text()
                        .content("Message!")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)
                        .build(),
                COMPONENT
        );
    }

    @Test
    void adventureSoundTest() {
        assertEquals(sound()
                        .type(key("sound_1_key"))
                        .source(Sound.Source.BLOCK)
                        .volume(10)
                        .pitch(2)
                        .build(),
                SOUND_1
        );

        assertEquals(sound()
                        .type(key("sound_2_key"))
                        .source(Sound.Source.MASTER)
                        .volume(1)
                        .pitch(1)
                        .build(),
                SOUND_2
        );
    }

    @Test
    void durationTest() {
        assertEquals(Duration.ofSeconds(10), DURATION_1);
        assertEquals(Duration.ofMinutes(60), DURATION_2);
        assertEquals(Duration.ofHours(40), DURATION_3);
        assertEquals(Duration.ofDays(365), DURATION_4);
    }

    @Test
    void distanceTest() {
        assertEquals(Distance.ofBlocks(8), DISTANCE_1);
        assertEquals(Distance.ofChunks(20), DISTANCE_2);
        assertEquals(Distance.ofRegions(100), DISTANCE_3);
    }

    @Test
    void locationTest() {
        assertEquals(ImmutableLocation.copyOf(world.getSpawnLocation()), LOCATION);
    }

    @Test
    void intTest() {
        assertEquals(1, INT);
    }

    @Test
    void longTest() {
        assertEquals(2, LONG);
    }

    @Test
    void doubleTest() {
        assertEquals(3.5, DOUBLE);
    }

    @Test
    void booleanTest() {
        assertTrue(BOOLEAN);
    }

    @Test
    void listTest() {
        assertEquals(List.of("f", "s", "t"), LIST);
    }

    @Test
    void fastutilIntListTest() {
        assertEquals(IntList.of(0, 5, 10, 15, 20), INT_LIST);
    }

    @Test
    void fastutilLongListTest() {
        assertEquals(LongList.of(10, 20, 30, 40), LONG_LIST);
    }

    @Test
    void fastutilDoubleListTest() {
        assertEquals(DoubleList.of(0.5, 1.5, 2.5), DOUBLE_LIST);
    }

    @Test
    void fastutilBooleanListTest() {
        assertEquals(BooleanList.of(true, false, true, true), BOOLEAN_LIST);
    }

    @Test
    void subClassTest() {
        assertEquals("expected", SubClass.VALUE);
    }

    public static String STRING_1 = "default";
    public static String STRING_2;
    public static String STRING_3;

    public static Component COMPONENT;

    public static Sound SOUND_1;
    public static Sound SOUND_2;

    public static Duration DURATION_1;
    public static Duration DURATION_2;
    public static Duration DURATION_3;
    public static Duration DURATION_4;

    public static Distance DISTANCE_1;
    public static Distance DISTANCE_2;
    public static Distance DISTANCE_3;

    public static ImmutableLocation LOCATION;

    public static int INT;
    public static long LONG;
    public static double DOUBLE;
    public static boolean BOOLEAN;

    public static List<String> LIST;

    public static IntList INT_LIST;
    public static LongList LONG_LIST;
    public static DoubleList DOUBLE_LIST;
    public static BooleanList BOOLEAN_LIST;

    public static final class SubClass {
        public static String VALUE;
    }
}

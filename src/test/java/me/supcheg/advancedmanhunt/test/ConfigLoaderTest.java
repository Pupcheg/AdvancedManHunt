package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.structure.DummyContainerAdapter;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("CanBeFinal")
public class ConfigLoaderTest {

    private static World world;

    @SneakyThrows
    @BeforeAll
    static void setup() {
        MockBukkit.mock();

        world = WorldCreator.name("world").createWorld();

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try (Reader reader = Files.newBufferedReader(Path.of("build/resources/test/config_loader_test.yml"))) {
            yamlConfiguration.load(reader);
        }

        new ConfigLoader(new DummyContainerAdapter())
                .load(yamlConfiguration, ConfigLoaderTest.class);
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
    void locationTest() {
        assertEquals(world.getSpawnLocation(), LOCATION);
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
        assertEquals(INT_LIST, IntList.of(0, 5, 10, 15, 20));
    }

    @Test
    void fastutilLongListTest() {
        assertEquals(LONG_LIST, LongList.of(10, 20, 30, 40));
    }

    @Test
    void fastutilDoubleListTest() {
        assertEquals(DOUBLE_LIST, DoubleList.of(0.5, 1.5, 2.5));
    }

    @Test
    void fastutilBooleanListTest() {
        assertEquals(BOOLEAN_LIST, BooleanList.of(true, false, true, true));
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

    public static Location LOCATION;

    public static int INT;
    public static long LONG;
    public static double DOUBLE;
    public static boolean BOOLEAN;

    public static List<String> LIST;

    public static IntList INT_LIST;
    public static LongList LONG_LIST;
    public static DoubleList DOUBLE_LIST;
    public static BooleanList BOOLEAN_LIST;
}

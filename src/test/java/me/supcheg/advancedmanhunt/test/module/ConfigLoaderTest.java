package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongList;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.config.ConfigLoader;
import me.supcheg.advancedmanhunt.test.structure.TestPaperPlugin;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static net.kyori.adventure.key.Key.key;
import static net.kyori.adventure.sound.Sound.sound;
import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("CanBeFinal")
public class ConfigLoaderTest {

    @BeforeEach
    void setup() {
        MockBukkit.mock();
    }

    @AfterEach
    void shutdown() {
        MockBukkit.unmock();
    }

    @Test
    void pathResolverTest() throws NoSuchFieldException {
        String path = ConfigLoader.resolveConfigPath(AdvancedManHuntConfig.TemplateLoad.class,
                AdvancedManHuntConfig.TemplateLoad.class.getField("THREAD_POOL_SIZE"));
        assertEquals("template_load.thread_pool_size", path);
    }

    @SneakyThrows
    @Test
    void fullLoadTest() {
        World world = WorldCreator.name("world").createWorld();
        assertNotNull(world);

        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try (Reader reader = Files.newBufferedReader(Path.of("build/resources/test/config_loader_test.yml"))) {
            yamlConfiguration.load(reader);
        }

        new ConfigLoader(TestPaperPlugin.load())
                .load(yamlConfiguration, getClass());


        assertEquals("replaced", STRING_1);
        assertEquals("string", STRING_2);
        assertEquals("first\nsecond\nthird", STRING_3);

        assertEquals(text()
                        .content("Message!")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)
                        .build(),
                COMPONENT
        );

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

        assertEquals(Duration.ofSeconds(10), DURATION_1);
        assertEquals(Duration.ofMinutes(60), DURATION_2);
        assertEquals(Duration.ofHours(40), DURATION_3);
        assertEquals(Duration.ofDays(365), DURATION_4);

        assertEquals(world.getSpawnLocation(), LOCATION);

        assertEquals(1, INT);
        assertEquals(2, LONG);
        assertEquals(3.5, DOUBLE);
        assertTrue(BOOLEAN);

        assertEquals(List.of("f", "s", "t"), LIST);

        assertEquals(INT_LIST, IntList.of(0, 5, 10, 15, 20));
        assertEquals(LONG_LIST, LongList.of(10, 20, 30, 40));
        assertEquals(DOUBLE_LIST, DoubleList.of(0.5, 1.5, 2.5));
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

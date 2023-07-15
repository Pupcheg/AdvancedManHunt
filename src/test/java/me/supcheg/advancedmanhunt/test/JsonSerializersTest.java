package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocation;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonSerializersTest {
    private static RandomGenerator random;
    private static JsonSerializer jsonSerializer;
    private static Gson gson;

    @BeforeAll
    static void beforeAll() {
        MockBukkit.mock();
        random = new SecureRandom();
        jsonSerializer = new JsonSerializer();
        gson = new GsonBuilder().registerTypeAdapterFactory(jsonSerializer).setPrettyPrinting().create();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @Test
    void nullTest() {
        for (Type type : jsonSerializer.getSupportedTypes()) {
            assertEquals("null", gson.toJson(null, type));
            assertNull(gson.fromJson("null", type));
        }
    }

    @Test
    void compactKeyedCoordTest() {
        boolean oldValue = AdvancedManHuntConfig.Serialization.COMPACT_COORDS;

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = true;
        roundTrip(newKeyedCoord());

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = oldValue;
    }

    @Test
    void notCompactKeyedCoordTest() {
        boolean oldValue = AdvancedManHuntConfig.Serialization.COMPACT_COORDS;

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = false;
        roundTrip(newKeyedCoord());

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = oldValue;
    }

    @Test
    void distanceTest() {
        roundTrip(Distance.ofChunks(newInt()));
    }

    @Test
    void locationTest() {
        roundTrip(newLocation());
    }

    @Test
    void regionTemplateTest() {
        roundTrip(
                new Template(
                        "amh:test_key",
                        Distance.ofBlocks(newInt()),
                        Path.of("ok"),
                        List.of(newSpawnLocation(), newSpawnLocation(), newSpawnLocation())
                )
        );
    }

    @Test
    void gameRegionTest() {
        WorldReference worldReference = WorldReference.of(
                Objects.requireNonNull(WorldCreator.name("world")
                        .createWorld())
        );

        roundTrip(new GameRegion(worldReference, newKeyedCoord(), newKeyedCoord()));
    }

    @Test
    void cachedSpawnLocationTest() {
        roundTrip(newSpawnLocation());
    }

    private void roundTrip(@NotNull Object expected) {
        Type type = expected.getClass();
        String json = gson.toJson(expected, type);

        Object actual = gson.fromJson(json, type);
        assertEquals(expected, actual);
    }

    @NotNull
    @Contract(" -> new")
    private CachedSpawnLocation newSpawnLocation() {
        return new CachedSpawnLocation(
                newLocation(),
                new Location[]{newLocation(), newLocation()},
                newLocation()
        );
    }

    @NotNull
    @Contract(" -> new")
    private KeyedCoord newKeyedCoord() {
        return KeyedCoord.of(newInt(), newInt());
    }

    @NotNull
    @Contract(" -> new")
    private Location newLocation() {
        return new Location(null, newInt(), newInt(), newInt(), newInt(), newInt());
    }

    private int newInt() {
        return random.nextInt(-128, 128);
    }
}

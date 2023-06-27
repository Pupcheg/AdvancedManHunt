package me.supcheg.advancedmanhunt.test.module;

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
    void nullCheck() {
        for (Type type : jsonSerializer.getSupportedTypes()) {
            assertEquals("null", gson.toJson(null, type));
            assertNull(gson.fromJson("null", type));
        }
    }

    @Test
    void keyedCoord() {
        boolean oldValue = AdvancedManHuntConfig.Serialization.COMPACT_COORDS;

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = true;
        roundTrip(randomKeyedCoord());
        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = false;
        roundTrip(randomKeyedCoord());

        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = oldValue;
    }

    @Test
    void distance() {
        roundTrip(Distance.ofChunks(randomInt()));
    }

    @Test
    void location() {
        boolean oldValue = AdvancedManHuntConfig.Serialization.SHORT_LOCATIONS;

        AdvancedManHuntConfig.Serialization.SHORT_LOCATIONS = true;
        roundTrip(randomLocation());
        AdvancedManHuntConfig.Serialization.SHORT_LOCATIONS = false;
        roundTrip(new Location(null, randomInt(), randomInt(), randomInt(), randomInt(), randomInt()));

        AdvancedManHuntConfig.Serialization.SHORT_LOCATIONS = oldValue;
    }

    @Test
    void regionTemplate() {
        roundTrip(
                new Template(
                        "amh:test_key",
                        Distance.ofBlocks(randomInt()),
                        Path.of("ok"),
                        List.of(randomSpawnLocation(), randomSpawnLocation(), randomSpawnLocation())
                )
        );
    }

    @Test
    void gameRegion() {
        WorldReference worldReference = WorldReference.of(
                Objects.requireNonNull(WorldCreator.name("world")
                        .createWorld())
        );

        roundTrip(new GameRegion(worldReference, randomKeyedCoord(), randomKeyedCoord()));
    }

    @Test
    void cachedSpawnLocation() {
        roundTrip(randomSpawnLocation());
    }

    private <T> void roundTrip(@NotNull T expected) {
        Type type = expected.getClass();
        String json = gson.toJson(expected, type);

        T actual = gson.fromJson(json, type);
        assertEquals(expected, actual);
    }

    @NotNull
    @Contract(" -> new")
    private CachedSpawnLocation randomSpawnLocation() {
        return new CachedSpawnLocation(
                randomLocation(),
                new Location[]{randomLocation(), randomLocation()},
                randomLocation()
        );
    }

    @NotNull
    @Contract(" -> new")
    private KeyedCoord randomKeyedCoord() {
        return KeyedCoord.of(randomInt(), randomInt());
    }

    @NotNull
    @Contract(" -> new")
    private Location randomLocation() {
        return new Location(null, randomInt(), randomInt(), randomInt());
    }

    private int randomInt() {
        return random.nextInt(-128, 128);
    }
}

package me.supcheg.advancedmanhunt.test;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.json.JsonSerializer;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.util.ThreadSafeRandom;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonSerializersTest {
    private static JsonSerializer jsonSerializer;
    private static Gson gson;

    @BeforeAll
    static void beforeAll() {
        MockBukkit.mock();
        jsonSerializer = new JsonSerializer();
        gson = new GsonBuilder().registerTypeAdapterFactory(jsonSerializer).create();
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
    private SpawnLocationFindResult newSpawnLocation() {
        return SpawnLocationFindResult.of(
                newLocation(),
                List.of(newLocation(), newLocation()),
                newLocation()
        );
    }

    @NotNull
    @Contract(" -> new")
    private ImmutableLocation newLocation() {
        return new ImmutableLocation((World) null, newInt(), newInt(), newInt(), newInt(), newInt());
    }

    private int newInt() {
        return ThreadSafeRandom.randomInt(1024);
    }
}

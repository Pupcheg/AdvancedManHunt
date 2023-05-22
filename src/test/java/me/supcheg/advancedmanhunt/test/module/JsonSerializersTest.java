package me.supcheg.advancedmanhunt.test.module;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.google.gson.Gson;
import me.supcheg.advancedmanhunt.config.AdvancedManHuntConfig;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.WorldReference;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocations;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocationsEntry;
import me.supcheg.advancedmanhunt.template.Template;
import me.supcheg.advancedmanhunt.test.structure.InjectingPaperPlugin;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

class JsonSerializersTest {
    private static RandomGenerator random;
    private static Gson gson;

    @BeforeAll
    static void beforeEach() {
        MockBukkit.mock();
        random = new SecureRandom();
        gson = InjectingPaperPlugin.load().getGson();
    }

    @AfterAll
    static void afterAll() {
        MockBukkit.unmock();
    }

    @Test
    void nullCheck() {
        List<Type> types = List.of(
                Template.class, KeyedCoord.class, Location.class,
                GameRegion.class, Distance.class,
                CachedSpawnLocations.class, CachedSpawnLocationsEntry.class
        );

        for (Type type : types) {
            Assertions.assertEquals("null", gson.toJson(null, type));
            Assertions.assertNull(gson.fromJson("null", type));
        }
    }

    @Test
    void keyedCoord() {
        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = true;
        roundTrip(randomKeyedCoord());
        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = false;
        roundTrip(randomKeyedCoord());
        AdvancedManHuntConfig.Serialization.COMPACT_COORDS = true;
    }

    @Test
    void distance() {
        roundTrip(Distance.ofChunks(randomInt()));
    }

    @Test
    void location() {
        roundTrip(randomLocation());
    }

    @Test
    void regionTemplate() {
        roundTrip(new Template("amh:test_key", Distance.ofBlocks(randomInt()), Path.of("ok")));
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
    void cachedSpawnLocations() {
        roundTrip(
                new CachedSpawnLocations(
                        random.nextLong(),
                        List.of(
                                new CachedSpawnLocationsEntry(
                                        randomLocation(),
                                        new Location[]{randomLocation(), randomLocation()},
                                        randomLocation()
                                ),
                                new CachedSpawnLocationsEntry(
                                        randomLocation(),
                                        new Location[]{randomLocation(), randomLocation()},
                                        randomLocation()
                                )
                        )
                )
        );
    }

    private <T> void roundTrip(@NotNull T expected) {
        Type type = expected.getClass();
        String json = gson.toJson(expected, type);

        T actual = gson.fromJson(json, type);
        Assertions.assertEquals(expected, actual);
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

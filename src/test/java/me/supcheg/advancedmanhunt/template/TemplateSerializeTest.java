package me.supcheg.advancedmanhunt.template;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.json.adapter.DistanceAdapter;
import me.supcheg.advancedmanhunt.json.adapter.ImmutableLocationAdapter;
import me.supcheg.advancedmanhunt.json.adapter.KeyAdapter;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.util.Keys;
import me.supcheg.advancedmanhunt.util.MapTypeAdapterFactory;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.supcheg.advancedmanhunt.coord.ImmutableLocation.immutableLocation;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TemplateSerializeTest {
    Gson gson;
    SerializedTemplate template;

    @BeforeEach
    void setup() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(
                        new MapTypeAdapterFactory()
                                .typeAdapter(ImmutableLocation.class, ImmutableLocationAdapter::new)
                                .typeAdapter(Distance.class, DistanceAdapter::new)
                                .typeAdapter(Key.class, KeyAdapter::new)
                )
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        template = new SerializedTemplate(
                Keys.key("namespace", "my_key"),
                Distance.ofRegions(1),
                List.of(
                        SpawnLocationFindResult.of(
                                immutableLocation().build(),
                                List.of(
                                        immutableLocation().build()
                                ),
                                immutableLocation().build()
                        )
                )
        );
    }

    @Test
    void serializeTemplateTest() {
        String serialized = gson.toJson(template, SerializedTemplate.class);
        SerializedTemplate deserialized = gson.fromJson(serialized, SerializedTemplate.class);
        assertEquals(template, deserialized);
    }
}

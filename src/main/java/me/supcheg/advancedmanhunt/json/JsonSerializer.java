package me.supcheg.advancedmanhunt.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import me.supcheg.advancedmanhunt.region.GameRegion;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocations;
import me.supcheg.advancedmanhunt.region.impl.CachedSpawnLocationFinder.CachedSpawnLocationsEntry;
import me.supcheg.advancedmanhunt.template.Template;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonSerializer implements TypeAdapterFactory {

    private final Map<Class<?>, Function<Gson, TypeAdapter<?>>> type2adapterMap;

    public JsonSerializer() {
        type2adapterMap = Maps.newHashMapWithExpectedSize(7);

        register(Template.class, TemplateSerializer::new);
        register(KeyedCoord.class, KeyedCoordSerializer::new);
        register(Location.class, LocationSerializer::new);
        register(GameRegion.class, GameRegionSerializer::new);
        register(Distance.class, DistanceSerializer::new);

        register(CachedSpawnLocations.class, CachedSpawnLocationsSerializer::new);
        register(CachedSpawnLocationsEntry.class, CachedSpawnLocationsEntrySerializer::new);
    }

    @NotNull
    @Contract(value = "-> new", pure = true)
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapterFactory(new JsonSerializer())
                .create();
    }

    public <T> void register(@NotNull Class<T> type, @NotNull Supplier<TypeAdapter<T>> constructor) {
        type2adapterMap.put(type, gson -> constructor.get());
    }

    public <T> void register(@NotNull Class<T> type, @NotNull Function<Gson, TypeAdapter<T>> constructor) {
        type2adapterMap.put(type, constructor::apply);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        var typeAdapterConstructor = type2adapterMap.get(type.getRawType());
        if (typeAdapterConstructor != null) {
            return (TypeAdapter<T>) typeAdapterConstructor.apply(gson).nullSafe();
        }
        return null;
    }
}

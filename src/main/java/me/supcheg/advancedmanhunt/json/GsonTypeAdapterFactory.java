package me.supcheg.advancedmanhunt.json;

import com.google.gson.Gson;
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
import org.jetbrains.annotations.NotNull;

public class GsonTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        TypeAdapter<?> typeAdapter = null;

        Class<?> rawType = type.getRawType();

        if (rawType == Template.class) {
            typeAdapter = new RegionTemplateSerializer(gson);
        } else if (rawType == KeyedCoord.class) {
            typeAdapter = new KeyedCoordSerializer();
        } else if (rawType == Location.class) {
            typeAdapter = new LocationSerializer();
        } else if (rawType == GameRegion.class) {
            typeAdapter = new GameRegionSerializer(gson);
        } else if (rawType == CachedSpawnLocations.class) {
            typeAdapter = new CachedSpawnLocationsSerializer(gson);
        } else if (rawType == CachedSpawnLocationsEntry.class) {
            typeAdapter = new CachedSpawnLocationsEntrySerializer(gson);
        } else if (rawType == Distance.class) {
            typeAdapter = new DistanceSerializer();
        }

        return (TypeAdapter<T>) (typeAdapter == null ? null : typeAdapter.nullSafe());
    }
}

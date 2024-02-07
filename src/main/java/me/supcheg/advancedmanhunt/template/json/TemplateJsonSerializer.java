package me.supcheg.advancedmanhunt.template.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import me.supcheg.advancedmanhunt.coord.Distance;
import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.region.SpawnLocationFindResult;
import me.supcheg.advancedmanhunt.template.Template;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class TemplateJsonSerializer implements TypeAdapterFactory {

    private final Map<Type, Function<Gson, TypeAdapter<?>>> type2adapterMap;

    public TemplateJsonSerializer() {
        type2adapterMap = Maps.newHashMapWithExpectedSize(4);

        register(Template.class, TemplateSerializer::new);
        register(ImmutableLocation.class, new ImmutableLocationSerializer());
        register(Distance.class, new DistanceSerializer());
        register(SpawnLocationFindResult.class, SpawnLocationFindResultSerializer::new);
    }

    public <T> void register(@NotNull Class<T> type, @NotNull TypeAdapter<T> typeAdapter) {
        type2adapterMap.put(type, __ -> typeAdapter);
    }

    public <T> void register(@NotNull Class<T> type, @NotNull Function<Gson, TypeAdapter<T>> constructor) {
        type2adapterMap.put(type, constructor::apply);
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        Function<Gson, TypeAdapter<?>> typeAdapter = type2adapterMap.get(type.getType());
        return typeAdapter == null ? null : uncheckedCast(typeAdapter.apply(gson).nullSafe());
    }
}

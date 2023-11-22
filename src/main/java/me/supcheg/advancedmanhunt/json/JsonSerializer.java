package me.supcheg.advancedmanhunt.json;

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
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonSerializer implements TypeAdapterFactory {

    private final Map<Class<?>, Function<Gson, TypeAdapter<?>>> type2adapterMap;

    public JsonSerializer() {
        type2adapterMap = Maps.newHashMapWithExpectedSize(4);

        register(Template.class, TemplateSerializer::new);
        register(ImmutableLocation.class, ImmutableLocationSerializer::new);
        register(Distance.class, DistanceSerializer::new);
        register(SpawnLocationFindResult.class, SpawnLocationFindResultSerializer::new);
    }

    @NotNull
    @UnmodifiableView
    public Set<Class<?>> getSupportedTypes() {
        return Collections.unmodifiableSet(type2adapterMap.keySet());
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
        for (Map.Entry<Class<?>, Function<Gson, TypeAdapter<?>>> e : type2adapterMap.entrySet()) {
            if (type.getRawType().isAssignableFrom(e.getKey())) {
                return (TypeAdapter<T>) e.getValue().apply(gson).nullSafe();
            }
        }
        return null;
    }
}

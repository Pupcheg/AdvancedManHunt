package me.supcheg.advancedmanhunt.util;

import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class MapTypeAdapterFactory implements TypeAdapterFactory {
    private static final TypeAdapterConstructor ALWAYS_NULL = new TypeAdapterConstructor() {
        @Nullable
        @Override
        public TypeAdapter<?> construct(@NotNull Gson gson) {
            return null;
        }

        @Nullable
        @Override
        public <T> TypeAdapter<T> constructNullSafe(@NotNull Gson gson) {
            return null;
        }
    };

    private final Map<Type, TypeAdapterConstructor> type2adapter;

    public MapTypeAdapterFactory() {
        this(new HashMap<>());
    }

    public MapTypeAdapterFactory(int expectedSize) {
        this(Maps.newHashMapWithExpectedSize(expectedSize));
    }

    private MapTypeAdapterFactory(@NotNull Map<Type, TypeAdapterConstructor> type2adapter) {
        this.type2adapter = type2adapter;
    }

    @NotNull
    @CanIgnoreReturnValue
    @Contract("_, _ -> this")
    public <T> MapTypeAdapterFactory typeAdapter(@NotNull Class<T> type, @NotNull Supplier<TypeAdapter<T>> supplier) {
        type2adapter.put(type, __ -> supplier.get());
        return this;
    }

    @NotNull
    @CanIgnoreReturnValue
    @Contract("_, _ -> this")
    public <T> MapTypeAdapterFactory typeAdapter(@NotNull Class<T> type, @NotNull Function<Gson, TypeAdapter<T>> function) {
        type2adapter.put(type, function::apply);
        return this;
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        return type2adapter.getOrDefault(type.getType(), ALWAYS_NULL).constructNullSafe(gson);
    }

    @FunctionalInterface
    private interface TypeAdapterConstructor {
        @UnknownNullability
        TypeAdapter<?> construct(@NotNull Gson gson);

        @Nullable
        default <T> TypeAdapter<T> constructNullSafe(@NotNull Gson gson) {
            return uncheckedCast(construct(gson).nullSafe());
        }
    }
}

package me.supcheg.advancedmanhunt.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static me.supcheg.advancedmanhunt.util.Unchecked.uncheckedCast;

public class MapTypeAdapterFactory implements TypeAdapterFactory {
    private final Map<Type, Function<Gson, TypeAdapter<?>>> type2adapterMap = new HashMap<>();

    @NotNull
    @CanIgnoreReturnValue
    @Contract("_, _ -> this")
    public <T> MapTypeAdapterFactory typeAdapter(@NotNull Class<T> type, @NotNull Supplier<TypeAdapter<T>> supplier) {
        type2adapterMap.put(type, __ -> supplier.get());
        return this;
    }

    @NotNull
    @CanIgnoreReturnValue
    @Contract("_, _ -> this")
    public <T> MapTypeAdapterFactory typeAdapter(@NotNull Class<T> type, @NotNull Function<Gson, TypeAdapter<T>> function) {
        type2adapterMap.put(type, uncheckedCast(function));
        return this;
    }

    @Nullable
    @Override
    public <T> TypeAdapter<T> create(@NotNull Gson gson, @NotNull TypeToken<T> type) {
        Function<Gson, TypeAdapter<?>> typeAdapter = type2adapterMap.get(type.getType());
        return uncheckedCast(typeAdapter == null ? null : typeAdapter.apply(gson).nullSafe());
    }
}

package me.supcheg.advancedmanhunt.config.serializer;

import io.papermc.paper.math.Position;
import lombok.SneakyThrows;
import me.supcheg.advancedmanhunt.util.PositionAdapter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class PositionSerializer<T extends Position> extends ScalarSerializer<T> {

    private final PositionAdapter<T> adapter;

    public PositionSerializer(@NotNull Class<T> type, @NotNull PositionAdapter<T> adapter) {
        super(type);
        this.adapter = adapter;
    }

    @SneakyThrows
    @Override
    public T deserialize(Type type, Object value) {
        return adapter.deserialize(String.valueOf(value));
    }

    @SneakyThrows
    @Override
    protected Object serialize(T item, Predicate<Class<?>> typeSupported) {
        return adapter.serialize(item);
    }
}

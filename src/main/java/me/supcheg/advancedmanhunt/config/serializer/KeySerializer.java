package me.supcheg.advancedmanhunt.config.serializer;

import net.kyori.adventure.key.Key;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class KeySerializer extends ScalarSerializer<Key> {
    public KeySerializer() {
        super(Key.class);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key deserialize(Type type, Object value) {
        return Key.key(String.valueOf(value));
    }

    @Override
    protected Object serialize(Key item, Predicate<Class<?>> typeSupported) {
        return item.asString();
    }
}

package me.supcheg.advancedmanhunt.config.serializer;

import me.supcheg.advancedmanhunt.coord.ImmutableLocation;
import me.supcheg.advancedmanhunt.coord.ImmutableLocations;
import org.spongepowered.configurate.serialize.ScalarSerializer;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class ImmutableLocationSerializer extends ScalarSerializer<ImmutableLocation> {
    public ImmutableLocationSerializer() {
        super(ImmutableLocation.class);
    }

    @Override
    public ImmutableLocation deserialize(Type type, Object value) {
        return ImmutableLocations.parseLocation(String.valueOf(value));
    }

    @Override
    protected Object serialize(ImmutableLocation item, Predicate<Class<?>> typeSupported) {
        return ImmutableLocations.serializeLocation(item);
    }
}

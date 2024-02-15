package me.supcheg.advancedmanhunt.config.serializer;

import me.supcheg.advancedmanhunt.coord.Distance;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DistanceSerializer extends ScalarSerializer<Distance> {
    private final String pattern = "\\d+[bcr]";
    private final Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

    public DistanceSerializer() {
        super(Distance.class);
    }

    @Override
    public Distance deserialize(Type type, Object value) throws SerializationException {
        String raw = String.valueOf(value);

        if (!compiledPattern.matcher(raw).matches()) {
            throw new SerializationException("%s doesn't matches %s pattern".formatted(raw, pattern));
        }

        int distance = Integer.parseInt(raw.substring(0, raw.length() - 1));
        return switch (Character.toLowerCase(raw.charAt(raw.length() - 1))) {
            case 'b' -> Distance.ofBlocks(distance);
            case 'c' -> Distance.ofChunks(distance);
            case 'r' -> Distance.ofRegions(distance);
            default -> throw new IllegalStateException("Unreachable");
        };
    }

    @Override
    protected Object serialize(Distance item, Predicate<Class<?>> typeSupported) {
        return item.getBlocks() + "b";
    }
}

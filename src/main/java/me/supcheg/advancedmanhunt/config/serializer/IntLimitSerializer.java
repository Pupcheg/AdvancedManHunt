package me.supcheg.advancedmanhunt.config.serializer;

import me.supcheg.advancedmanhunt.config.IntLimit;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IntLimitSerializer extends ScalarSerializer<IntLimit> {
    private final String pattern = "\\d+-\\d+";
    private final Pattern compiledPattern = Pattern.compile(pattern);

    public IntLimitSerializer() {
        super(IntLimit.class);
    }

    @Override
    public IntLimit deserialize(Type type, Object value) throws SerializationException {
        String raw = String.valueOf(value);

        if (!compiledPattern.matcher(raw).matches()) {
            throw new SerializationException("%s doesn't matches %s pattern".formatted(raw, pattern));
        }

        int separatorIndex = raw.indexOf('-');

        int minValue = Integer.parseInt(raw.substring(0, separatorIndex));
        int maxValue = Integer.parseInt(raw.substring(separatorIndex + 1));

        return IntLimit.of(minValue, maxValue);
    }

    @Override
    protected Object serialize(IntLimit item, Predicate<Class<?>> typeSupported) {
        return item.getMinValue() + "-" + item.getMaxValue();
    }
}

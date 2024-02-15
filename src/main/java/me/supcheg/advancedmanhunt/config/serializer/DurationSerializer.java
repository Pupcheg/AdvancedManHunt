package me.supcheg.advancedmanhunt.config.serializer;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DurationSerializer extends ScalarSerializer<Duration> {
    private final String pattern = "\\d+[dhms]";
    private final Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

    public DurationSerializer() {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(Type type, Object value) throws SerializationException {
        String raw = String.valueOf(value);

        if (!compiledPattern.matcher(raw).matches()) {
            throw new SerializationException("%s doesn't matches %s pattern".formatted(raw, pattern));
        }

        int duration = Integer.parseInt(raw.substring(0, raw.length() - 1));
        return switch (Character.toLowerCase(raw.charAt(raw.length() - 1))) {
            case 'd' -> Duration.ofDays(duration);
            case 'h' -> Duration.ofHours(duration);
            case 'm' -> Duration.ofMinutes(duration);
            case 's' -> Duration.ofSeconds(duration);
            default -> throw new IllegalStateException("Unreachable");
        };
    }

    @Override
    protected Object serialize(Duration item, Predicate<Class<?>> typeSupported) {
        long days = item.toDays();
        if (days != 0) {
            return days + "d";
        }

        long hours = item.toHours();
        if (hours != 0) {
            return hours + "h";
        }

        long minutes = item.toMinutes();
        if (minutes != 0) {
            return minutes + "m";
        }

        return item.toSeconds() + "s";
    }
}

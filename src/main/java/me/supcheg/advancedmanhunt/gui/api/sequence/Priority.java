package me.supcheg.advancedmanhunt.gui.api.sequence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Priority implements Comparable<Priority> {
    public static final Priority LOWEST = new Priority(20);
    public static final Priority LOW = new Priority(15);
    public static final Priority NORMAL = new Priority(10);
    public static final Priority HIGH = new Priority(5);
    public static final Priority HIGHEST = new Priority(0);

    private static final Map<String, Priority> NAME_TO_PRIORITY = Map.of(
            "lowest", LOWEST,
            "low", LOW,
            "normal", NORMAL,
            "high", HIGH,
            "highest", HIGHEST
    );

    @Nullable
    public static Priority fromName(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        return NAME_TO_PRIORITY.get(name.toLowerCase());
    }

    @NotNull
    public static Priority fromValue(int value) {
        return switch (value) {
            case 0 -> HIGHEST;
            case 5 -> HIGH;
            case 10 -> NORMAL;
            case 15 -> LOW;
            case 20 -> LOWEST;
            default -> new Priority(value);
        };
    }

    @NotNull
    public Priority earlier(int value) {
        return fromValue(this.value - value);
    }

    @NotNull
    public Priority later(int value) {
        return fromValue(this.value + value);
    }

    private final int value;

    @Override
    public int compareTo(@NotNull Priority o) {
        return Integer.compare(value, o.value);
    }
}

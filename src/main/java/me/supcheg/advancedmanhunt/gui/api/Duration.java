package me.supcheg.advancedmanhunt.gui.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Duration {
    public static final int INFINITY_VALUE = -1;
    public static final int OVER_VALUE = 0;

    public static final Duration INFINITY = new Duration(INFINITY_VALUE);
    public static final Duration OVER = new Duration(OVER_VALUE);

    private final int ticks;

    @NotNull
    public static Duration ofTicks(int ticks) {
        if (ticks < -1) {
            throw new IllegalArgumentException("" + ticks);
        }

        return switch (ticks) {
            case -1 -> INFINITY;
            case 0 -> OVER;
            default -> new Duration(ticks);
        };
    }

    @NotNull
    public static Duration ofSeconds(int seconds) {
        return ofTicks(seconds * 20);
    }

    public boolean isOver() {
        return ticks == OVER_VALUE;
    }

    public boolean isInfinity() {
        return ticks == INFINITY_VALUE;
    }
}

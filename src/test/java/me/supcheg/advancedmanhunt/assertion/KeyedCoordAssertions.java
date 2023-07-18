package me.supcheg.advancedmanhunt.assertion;

import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class KeyedCoordAssertions {
    private KeyedCoordAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertInBoundInclusive(@NotNull KeyedCoord coord, @NotNull KeyedCoord start, @NotNull KeyedCoord end) {
        assertTrue(
                CoordUtil.isInBoundInclusive(coord, start, end),
                () -> "%s is not in %s %s".formatted(coord, start.toInclusiveString(), end.toInclusiveString())
        );
    }
}

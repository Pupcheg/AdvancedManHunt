package me.supcheg.advancedmanhunt.assertion;

import me.supcheg.advancedmanhunt.coord.Coords;
import me.supcheg.advancedmanhunt.coord.Coord;
import org.jetbrains.annotations.NotNull;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class KeyedCoordAssertions {
    private KeyedCoordAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertInBoundInclusive(@NotNull Coord coord, @NotNull Coord start, @NotNull Coord end) {
        assertTrue(
                Coords.isInBoundInclusive(coord, start, end),
                () -> "%s is not in %s %s".formatted(coord, start.toInclusiveString(), end.toInclusiveString())
        );
    }
}

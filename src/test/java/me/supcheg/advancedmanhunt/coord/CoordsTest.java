package me.supcheg.advancedmanhunt.coord;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.IntUnaryOperator;

import static me.supcheg.advancedmanhunt.coord.Coords.checkBound;
import static me.supcheg.advancedmanhunt.coord.Coords.isInBoundExclusive;
import static me.supcheg.advancedmanhunt.coord.Coords.isInBoundInclusive;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoordsTest {
    @Test
    void chunkFromBlockTest() {
        assertResultEquals(Coord.coord(0, 0), Coord.coord(15, 15), Coords::getChunkFromBlock);
        assertResultEquals(Coord.coord(1, 1), Coord.coord(18, 25), Coords::getChunkFromBlock);
        assertResultEquals(Coord.coord(-1, -1), Coord.coord(-10, -1), Coords::getChunkFromBlock);
    }

    @Test
    void regionFromChunkTest() {
        assertResultEquals(Coord.coord(0, 0), Coord.coord(31, 31), Coords::getRegionFromChunk);
        assertResultEquals(Coord.coord(1, 2), Coord.coord(32, 64), Coords::getRegionFromChunk);
        assertResultEquals(Coord.coord(-1, -4), Coord.coord(-30, -120), Coords::getRegionFromChunk);
    }

    @Test
    void firstBlockInChunkTest() {
        assertResultEquals(Coord.coord(0, 0), Coord.coord(0, 0), Coords::getFirstBlockInChunk);
        assertResultEquals(Coord.coord(48, 48), Coord.coord(3, 3), Coords::getFirstBlockInChunk);
        assertResultEquals(Coord.coord(-8192, -16), Coord.coord(-512, -1), Coords::getFirstBlockInChunk);
    }

    @Test
    void firstChunkInRegionTest() {
        assertResultEquals(Coord.coord(0, 0), Coord.coord(0, 0), Coords::getFirstChunkInRegion);
        assertResultEquals(Coord.coord(96, 96), Coord.coord(3, 3), Coords::getFirstChunkInRegion);
        assertResultEquals(Coord.coord(-96, -96), Coord.coord(-3, -3), Coords::getFirstChunkInRegion);
    }

    @Test
    void isInBoundInclusiveTest() {
        assertTrue(isInBoundInclusive(Coord.coord(15, 15), Coord.coord(13, 9), Coord.coord(16, 15)));
        assertFalse(isInBoundInclusive(Coord.coord(16, 16), Coord.coord(-3, -3), Coord.coord(15, 32)));
    }

    @Test
    void isInBoundExclusiveTest() {
        assertTrue(isInBoundExclusive(Coord.coord(32, 16), Coord.coord(16, 8), Coord.coord(128, 32)));
        assertFalse(isInBoundExclusive(Coord.coord(32, 32), Coord.coord(32, 32), Coord.coord(128, 128)));
    }

    @Test
    void boundCheckTest() {
        assertDoesNotThrow(
                () -> checkBound(Coord.coord(0, 0), Coord.coord(32, 32))
        );
        assertThrows(
                Throwable.class,
                () -> checkBound(Coord.coord(0, 0), Coord.coord(-3, 13))
        );
    }

    private static void assertResultEquals(@NotNull Coord expected, @NotNull Coord actual,
                                           @NotNull IntUnaryOperator mapper) {
        assertEquals(expected, actual.map(mapper));
    }
}

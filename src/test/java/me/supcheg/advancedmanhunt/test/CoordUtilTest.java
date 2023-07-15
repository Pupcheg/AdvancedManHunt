package me.supcheg.advancedmanhunt.test;

import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;

import static me.supcheg.advancedmanhunt.coord.CoordUtil.*;
import static me.supcheg.advancedmanhunt.coord.KeyedCoord.of;
import static org.junit.jupiter.api.Assertions.*;

class CoordUtilTest {
    @Test
    void getKeyTest() {
        assertEquals(of(16, 16), of(getKey(16, 16)));
    }

    @Test
    void chunkFromBlockTest() {
        assertResultEquals(of(0, 0), of(15, 15), CoordUtil::getChunkFromBlock);
        assertResultEquals(of(1, 1), of(18, 25), CoordUtil::getChunkFromBlock);
        assertResultEquals(of(-1, -1), of(-10, -1), CoordUtil::getChunkFromBlock);
    }

    @Test
    void regionFromChunkTest() {
        assertResultEquals(of(0, 0), of(31, 31), CoordUtil::getRegionFromChunk);
        assertResultEquals(of(1, 2), of(32, 64), CoordUtil::getRegionFromChunk);
        assertResultEquals(of(-1, -4), of(-30, -120), CoordUtil::getRegionFromChunk);
    }

    @Test
    void firstBlockInChunkTest() {
        assertResultEquals(of(0, 0), of(0, 0), CoordUtil::getFirstBlockInChunk);
        assertResultEquals(of(48, 48), of(3, 3), CoordUtil::getFirstBlockInChunk);
        assertResultEquals(of(-8192, -16), of(-512, -1), CoordUtil::getFirstBlockInChunk);
    }

    @Test
    void firstChunkInRegionTest() {
        assertResultEquals(of(0, 0), of(0, 0), CoordUtil::getFirstChunkInRegion);
        assertResultEquals(of(96, 96), of(3, 3), CoordUtil::getFirstChunkInRegion);
        assertResultEquals(of(-96, -96), of(-3, -3), CoordUtil::getFirstChunkInRegion);
    }

    @Test
    void isInBoundInclusiveTest() {
        assertTrue(isInBoundInclusive(of(15, 15), of(13, 9), of(16, 15)));
        assertFalse(isInBoundInclusive(of(16, 16), of(-3, -3), of(15, 32)));
    }

    @Test
    void isInBoundExclusiveTest() {
        assertTrue(isInBoundExclusive(of(32, 16), of(16, 8), of(128, 32)));
        assertFalse(isInBoundExclusive(of(32, 32), of(32, 32), of(128, 128)));
    }

    @Test
    void streamInclusiveTest() {
        assertEquals(4, streamInclusive(of(0, 0), of(1, 1)).count());
    }

    @Test
    void boundCheckTest() {
        assertDoesNotThrow(
                () -> checkBound(of(0, 0), of(32, 32))
        );
        assertThrows(
                Throwable.class,
                () -> checkBound(of(0, 0), of(-3, 13))
        );
    }

    private static void assertResultEquals(@NotNull KeyedCoord expected, @NotNull KeyedCoord actual,
                                           @NotNull UnaryOperator<KeyedCoord> mapper) {
        assertEquals(expected, mapper.apply(actual));
    }
}

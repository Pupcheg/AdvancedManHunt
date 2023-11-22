package me.supcheg.advancedmanhunt.test;

import me.supcheg.advancedmanhunt.coord.CoordUtil;
import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.function.IntUnaryOperator;

import static me.supcheg.advancedmanhunt.coord.CoordUtil.checkBound;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.getKey;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.isInBoundExclusive;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.isInBoundInclusive;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.streamInclusive;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoordUtilTest {
    @Test
    void getKeyTest() {
        assertEquals(KeyedCoord.of(16, 16), KeyedCoord.ofKey(getKey(16, 16)));
    }

    @Test
    void chunkFromBlockTest() {
        assertResultEquals(KeyedCoord.of(0, 0), KeyedCoord.of(15, 15), CoordUtil::getChunkFromBlock);
        assertResultEquals(KeyedCoord.of(1, 1), KeyedCoord.of(18, 25), CoordUtil::getChunkFromBlock);
        assertResultEquals(KeyedCoord.of(-1, -1), KeyedCoord.of(-10, -1), CoordUtil::getChunkFromBlock);
    }

    @Test
    void regionFromChunkTest() {
        assertResultEquals(KeyedCoord.of(0, 0), KeyedCoord.of(31, 31), CoordUtil::getRegionFromChunk);
        assertResultEquals(KeyedCoord.of(1, 2), KeyedCoord.of(32, 64), CoordUtil::getRegionFromChunk);
        assertResultEquals(KeyedCoord.of(-1, -4), KeyedCoord.of(-30, -120), CoordUtil::getRegionFromChunk);
    }

    @Test
    void firstBlockInChunkTest() {
        assertResultEquals(KeyedCoord.of(0, 0), KeyedCoord.of(0, 0), CoordUtil::getFirstBlockInChunk);
        assertResultEquals(KeyedCoord.of(48, 48), KeyedCoord.of(3, 3), CoordUtil::getFirstBlockInChunk);
        assertResultEquals(KeyedCoord.of(-8192, -16), KeyedCoord.of(-512, -1), CoordUtil::getFirstBlockInChunk);
    }

    @Test
    void firstChunkInRegionTest() {
        assertResultEquals(KeyedCoord.of(0, 0), KeyedCoord.of(0, 0), CoordUtil::getFirstChunkInRegion);
        assertResultEquals(KeyedCoord.of(96, 96), KeyedCoord.of(3, 3), CoordUtil::getFirstChunkInRegion);
        assertResultEquals(KeyedCoord.of(-96, -96), KeyedCoord.of(-3, -3), CoordUtil::getFirstChunkInRegion);
    }

    @Test
    void isInBoundInclusiveTest() {
        assertTrue(isInBoundInclusive(KeyedCoord.of(15, 15), KeyedCoord.of(13, 9), KeyedCoord.of(16, 15)));
        assertFalse(isInBoundInclusive(KeyedCoord.of(16, 16), KeyedCoord.of(-3, -3), KeyedCoord.of(15, 32)));
    }

    @Test
    void isInBoundExclusiveTest() {
        assertTrue(isInBoundExclusive(KeyedCoord.of(32, 16), KeyedCoord.of(16, 8), KeyedCoord.of(128, 32)));
        assertFalse(isInBoundExclusive(KeyedCoord.of(32, 32), KeyedCoord.of(32, 32), KeyedCoord.of(128, 128)));
    }

    @Test
    void streamInclusiveTest() {
        // Don't change to #count()
        assertEquals(4, streamInclusive(KeyedCoord.of(0, 0), KeyedCoord.of(1, 1)).mapToInt(o -> 1).sum());
    }

    @Test
    void boundCheckTest() {
        assertDoesNotThrow(
                () -> checkBound(KeyedCoord.of(0, 0), KeyedCoord.of(32, 32))
        );
        assertThrows(
                Throwable.class,
                () -> checkBound(KeyedCoord.of(0, 0), KeyedCoord.of(-3, 13))
        );
    }

    private static void assertResultEquals(@NotNull KeyedCoord expected, @NotNull KeyedCoord actual,
                                           @NotNull IntUnaryOperator mapper) {
        assertEquals(expected, actual.map(mapper));
    }
}

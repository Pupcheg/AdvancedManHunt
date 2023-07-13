package me.supcheg.advancedmanhunt.test.module;

import me.supcheg.advancedmanhunt.coord.KeyedCoord;
import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterators.size;
import static com.google.common.collect.Iterators.toArray;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.iterateInclusive;
import static me.supcheg.advancedmanhunt.coord.KeyedCoord.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordIteratorTest {
    @Test
    void sizeTest() {
        assertEquals(25, size(iterateInclusive(of(1, 2), of(5, 6))));
    }

    @Test
    void arrayTest() {
        assertArrayEquals(
                new KeyedCoord[]{of(1, 1), of(2, 1), of(1, 2), of(2, 2)},
                toArray(iterateInclusive(of(1, 1), of(2, 2)), KeyedCoord.class)
        );
    }
}

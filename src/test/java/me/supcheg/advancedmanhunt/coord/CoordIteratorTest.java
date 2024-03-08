package me.supcheg.advancedmanhunt.coord;

import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterators.size;
import static com.google.common.collect.Iterators.toArray;
import static me.supcheg.advancedmanhunt.coord.CoordUtil.iterateInclusive;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordIteratorTest {
    @Test
    void sizeTest() {
        assertEquals(25, size(iterateInclusive(KeyedCoord.of(1, 2), KeyedCoord.of(5, 6))));
    }

    @Test
    void arrayTest() {
        assertArrayEquals(
                new KeyedCoord[]{KeyedCoord.of(1, 1), KeyedCoord.of(2, 1), KeyedCoord.of(1, 2), KeyedCoord.of(2, 2)},
                toArray(iterateInclusive(KeyedCoord.of(1, 1), KeyedCoord.of(2, 2)), KeyedCoord.class)
        );
    }
}

package me.supcheg.advancedmanhunt.coord;

import org.junit.jupiter.api.Test;

import static com.google.common.collect.Iterators.size;
import static com.google.common.collect.Iterators.toArray;
import static me.supcheg.advancedmanhunt.coord.Coords.iterateRangeInclusive;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordIteratorTest {
    @Test
    void sizeTest() {
        assertEquals(25, size(iterateRangeInclusive(Coord.coord(1, 2), Coord.coord(5, 6))));
    }

    @Test
    void arrayTest() {
        assertArrayEquals(
                new Coord[]{Coord.coord(1, 1), Coord.coord(2, 1), Coord.coord(1, 2), Coord.coord(2, 2)},
                toArray(iterateRangeInclusive(Coord.coord(1, 1), Coord.coord(2, 2)), Coord.class)
        );
    }
}

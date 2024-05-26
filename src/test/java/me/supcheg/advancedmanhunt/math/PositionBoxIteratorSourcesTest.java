package me.supcheg.advancedmanhunt.math;

import io.papermc.paper.math.BlockPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.collect.Iterators.size;
import static io.papermc.paper.math.Position.block;
import static me.supcheg.advancedmanhunt.math.PositionBox.box;
import static me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources.XYZ;
import static me.supcheg.advancedmanhunt.math.PositionBoxIteratorSources.XZ;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionBoxIteratorSourcesTest {

    PositionBox box;
    long xzSize;
    long xyzSize;

    @BeforeEach
    void setup() {
        box = box(
                block(-3, -4, -5),
                block(6, 7, 8)
        );
        xzSize = 10 * 14;
        xyzSize = 10 * 12 * 14;
    }

    @Test
    void xzSizeTest() {
        assertEquals(xzSize, XZ.size(box));
    }

    @Test
    void xyzSizeTest() {
        assertEquals(xyzSize, XYZ.size(box));
    }

    @Test
    void xzIteratorSizeTest() {
        assertEquals(xzSize, size(XZ.iterator(box)));
    }

    @Test
    void xyzIteratorSizeTest() {
        assertEquals(xyzSize, size(XYZ.iterator(box)));
    }

    @Test
    void xzIteratorBoundTest() {
        Iterator<BlockPosition> it = XZ.iterator(box);
        for (long i = 0; i < xzSize; i++) {
            it.next();
        }
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void xyzIteratorBoundTest() {
        Iterator<BlockPosition> it = XYZ.iterator(box);
        for (long i = 0; i < xyzSize; i++) {
            it.next();
        }
        assertThrows(NoSuchElementException.class, it::next);
    }
}

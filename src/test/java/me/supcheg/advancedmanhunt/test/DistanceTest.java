package me.supcheg.advancedmanhunt.test;

import me.supcheg.advancedmanhunt.coord.Distance;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static me.supcheg.advancedmanhunt.coord.Distance.ofBlocks;
import static me.supcheg.advancedmanhunt.coord.Distance.ofChunks;
import static me.supcheg.advancedmanhunt.coord.Distance.ofRegions;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistanceTest {

    @Test
    void blocksTest() {
        assertEquals(4, ofBlocks(4).getBlocks());
        assertEquals(4, ofBlocks(4 * 16).getChunks());
        assertEquals(4, ofBlocks(4 * 16 * 32).getRegions());
    }

    @Test
    void chunksTest() {
        assertEquals(4 * 16, ofChunks(4).getBlocks());
        assertEquals(4, ofChunks(4).getChunks());
        assertEquals(4, ofChunks(4 * 32).getRegions());
    }

    @Test
    void regionsTest() {
        assertEquals(4 * 16 * 32, ofRegions(4).getBlocks());
        assertEquals(4 * 32, ofRegions(4).getChunks());
        assertEquals(4, ofRegions(4).getRegions());
    }

    @Test
    void addTest() {
        assertEquals(4 + 4, ofBlocks(4).addBlocks(4).getBlocks());
        assertEquals(4 + 4 * 16, ofBlocks(4).addChunks(4).getBlocks());
        assertEquals(4 + 4 * 16 * 32, ofBlocks(4).addRegions(4).getBlocks());
    }

    @Test
    void subtractTest() {
        assertEquals(256 - 4, ofBlocks(256).subtractBlocks(4).getBlocks());
        assertEquals(256 - 2 * 16, ofBlocks(256).subtractChunks(2).getBlocks());
        assertEquals(1024 - 2 * 16 * 32, ofBlocks(1024).subtractRegions(2).getBlocks());
    }

    @Test
    void roundTest() {
        assertEquals(2, ofChunks(2).addBlocks(5).getChunks());
        assertEquals(2, ofRegions(2).addChunks(5).getRegions());
    }

    @Test
    void exactTest() {
        assertEquals(2.5, ofBlocks(16 + 16 + 8).getExactChunks());
        assertEquals(2.5, ofChunks(32 + 32 + 16).getExactRegions());
    }

    @Test
    void fullTest() {
        assertTrue(ofChunks(2).isFullChunks());
        assertTrue(ofRegions(2).isFullRegions());

        assertFalse(ofBlocks(2).isFullChunks());
        assertFalse(ofChunks(15).isFullRegions());

    }

    @Test
    void compareTest() {
        assertTrue(ofBlocks(8).isLessThan(ofChunks(8)));
        assertTrue(ofChunks(1).isEquals(ofBlocks(16)));
        assertTrue(ofChunks(1).isGreaterThan(ofBlocks(15)));

        assertTrue(ofChunks(1).isEqualsOrLessThan(ofBlocks(16)));
        assertTrue(ofChunks(1).isEqualsOrLessThan(ofBlocks(17)));

        assertTrue(ofChunks(1).isEqualsOrGreaterThan(ofBlocks(16)));
        assertTrue(ofChunks(1).isEqualsOrGreaterThan(ofBlocks(15)));
    }

    @Test
    void sortTest() {
        assertArrayEquals(
                new Distance[]{ofBlocks(1), ofChunks(1), ofRegions(1)},
                Stream.of(ofRegions(1), ofChunks(1), ofBlocks(1)).sorted().toArray()
        );
    }
}

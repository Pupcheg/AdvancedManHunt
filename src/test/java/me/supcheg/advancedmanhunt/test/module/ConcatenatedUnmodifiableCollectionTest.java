package me.supcheg.advancedmanhunt.test.module;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static me.supcheg.advancedmanhunt.util.ConcatenatedUnmodifiableCollection.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcatenatedUnmodifiableCollectionTest {

    @Test
    void emptyTest() {
        assertTrue(of(emptySet(), emptySet()).isEmpty());
    }

    @Test
    void immutableTest() {
        assertEquals(2, of(singleton("abc"), singleton("def")).size());
    }

    @Test
    void singleMutatingTest() {
        var mutable = Stream.generate(() -> "obj").limit(5).collect(Collectors.toList());
        var immutable = of(emptySet(), mutable);
        assertArrayEquals(mutable.toArray(), immutable.toArray());

        mutable.add("obj");
        assertArrayEquals(mutable.toArray(), immutable.toArray());
    }
}

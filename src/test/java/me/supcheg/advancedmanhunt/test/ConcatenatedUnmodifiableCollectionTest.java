package me.supcheg.advancedmanhunt.test;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableCollection;
import static me.supcheg.advancedmanhunt.util.ConcatenatedUnmodifiableCollection.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcatenatedUnmodifiableCollectionTest {

    @Test
    void emptyTest() {
        assertTrue(of(emptySet(), emptySet()).isEmpty());
    }

    @Test
    void sizeTest() {
        assertEquals(35, of(newCollectionWithSize(15), newCollectionWithSize(20)).size());
    }

    @Test
    void viewLogicTest() {
        Collection<Object> mutable = new ArrayList<>();
        Collection<Object> concatenated = of(mutable, newCollectionWithSize(5));

        mutable.addAll(newCollectionWithSize(15));
        assertEquals(20, concatenated.size());
    }

    @NotNull
    @Unmodifiable
    @Contract(value = "_ -> new", pure = true)
    private Collection<Object> newCollectionWithSize(int size) {
        String[] array = new String[size];
        fill(array, "abc");
        return unmodifiableCollection(asList(array));
    }
}

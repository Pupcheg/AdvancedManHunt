package me.supcheg.advancedmanhunt.test;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.supcheg.advancedmanhunt.util.ConcatenatedUnmodifiableCollection.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcatenatedUnmodifiableCollectionTest {

    @Test
    void isEmptyTest() {
        assertTrue(newConcatenatedCollectionWithSizes(0, 0).isEmpty());
    }

    @Test
    void sizeTest() {
        assertEquals(35, newConcatenatedCollectionWithSizes(15, 20).size());
    }

    @Test
    void iteratorTest() {
        Iterator<Object> it = newConcatenatedCollectionWithSizes(40, 15).iterator();

        assertEquals(55, Iterators.size(it));
    }

    @Test
    void forEachTest() {
        AtomicInteger size = new AtomicInteger();
        newConcatenatedCollectionWithSizes(15, 40).forEach(o -> size.incrementAndGet());

        assertEquals(55, size.get());
    }

    @Test
    void streamTest() {
        int size = newConcatenatedCollectionWithSizes(5, 10).stream().mapToInt(o -> 1).sum();

        assertEquals(15, size);
    }

    @Test
    void parallelStreamTest() {
        int size = newConcatenatedCollectionWithSizes(5, 10).parallelStream().mapToInt(o -> 1).sum();

        assertEquals(15, size);
    }

    @Test
    void containsTest() {
        Collection<Object> collection = of(newCollectionWithSize(15), List.of("expected"));

        assertTrue(collection.contains("expected"));
        assertFalse(collection.contains("unexpected"));
    }

    @Test
    void removeThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        assertThrows(Throwable.class, () -> collection.remove("obj"));
    }

    @Test
    void addThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        assertThrows(Throwable.class, () -> collection.add("obj"));
    }

    @Test
    void addAllThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        Collection<Object> toAdd = newCollectionWithSize(2);
        assertThrows(Throwable.class, () -> collection.addAll(toAdd));
    }

    @Test
    void removeAllThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        Collection<Object> toRemove = newCollectionWithSize(2);
        assertThrows(Throwable.class, () -> collection.removeAll(toRemove));
    }

    @Test
    void removeIfThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        assertThrows(Throwable.class, () -> collection.removeIf(o -> true));
    }

    @Test
    void retainAllThrowTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        Collection<Object> toRetain = newCollectionWithSize(2);
        assertThrows(Throwable.class, () -> collection.retainAll(toRetain));
    }

    @Test
    void clearTest() {
        Collection<Object> collection = newConcatenatedCollectionWithSizes(2, 2);
        assertThrows(Throwable.class, collection::clear);
    }

    @Test
    void viewLogicTest() {
        Collection<Object> mutable = new ArrayList<>();
        Collection<Object> concatenated = of(mutable, newCollectionWithSize(5));

        mutable.addAll(newCollectionWithSize(15));
        assertEquals(20, concatenated.size());
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private Collection<Object> newConcatenatedCollectionWithSizes(int firstSize, int secondSize) {
        return of(newCollectionWithSize(firstSize), newCollectionWithSize(secondSize));
    }

    @NotNull
    @Unmodifiable
    @Contract(value = "_ -> new", pure = true)
    private Collection<Object> newCollectionWithSize(int size) {
        String[] array = new String[size];
        Arrays.fill(array, "abc");
        return List.of(array);
    }
}

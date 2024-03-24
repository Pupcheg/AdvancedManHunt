package me.supcheg.advancedmanhunt.player.random;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static me.supcheg.advancedmanhunt.random.ThreadSafeRandom.randomElement;
import static me.supcheg.advancedmanhunt.random.ThreadSafeRandom.shuffled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadSafeRandomTest {
    @Test
    void randomElementTest() {
        List<String> list = of("abc", "def");
        String randomElement = randomElement(list);

        assertTrue(list.contains(randomElement));
    }

    @Test
    void randomElementEmptyThrowTest() {
        List<Object> list = emptyList();
        assertThrows(NoSuchElementException.class, () -> randomElement(list));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void randomElementNullTest() {
        assertThrows(NullPointerException.class, () -> randomElement(null));
    }

    @Test
    void shuffledTest() {
        List<String> list = of("abc", "def");
        assertTrue(shuffled(list).containsAll(list));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shuffledNullTest() {
        assertThrows(NullPointerException.class, () -> shuffled(null));
    }

    @Test
    void shuffledEmptyTest() {
        assertEquals(emptyList(), shuffled(emptyList()));
    }
}

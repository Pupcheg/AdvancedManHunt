package me.supcheg.advancedmanhunt.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadSafeRandom {

    /**
     * Gets random element from {@code list} using {@link ThreadLocalRandom}
     *
     * @return random element from {@code list} <p>possible {@code null} - if list contain empty elements
     * @throws NoSuchElementException if {@code list} is empty
     * @throws NullPointerException   if {@code list} is {@code null}
     */
    @UnknownNullability
    public static <T> T randomElement(@NotNull List<@Nullable T> list) {
        Objects.requireNonNull(list, "list");
        if (list.isEmpty()) {
            throw new NoSuchElementException("Can't get random element from empty list");
        }
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /**
     * Copies and shuffles {@code list}
     *
     * @return Shuffled copy of {@code list}
     * @throws NullPointerException if {@code list} is null
     */
    @NotNull
    @Unmodifiable
    public static <T> List<@Nullable T> shuffled(@NotNull List<@Nullable T> list) {
        Objects.requireNonNull(list, "list");
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, ThreadLocalRandom.current());
        return Collections.unmodifiableList(copy);
    }
}

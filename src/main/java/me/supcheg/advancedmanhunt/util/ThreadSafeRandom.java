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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadSafeRandom {

    @UnknownNullability
    public static <T> T randomElement(@NotNull List<@Nullable T> list) {
        Objects.requireNonNull(list, "list");
        if (list.isEmpty()) {
            throw new NoSuchElementException("Can't get random element from empty list");
        }
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

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

    @NotNull
    public static UUID randomUniqueId() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong());
    }
}

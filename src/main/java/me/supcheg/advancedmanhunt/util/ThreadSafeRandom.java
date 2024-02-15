package me.supcheg.advancedmanhunt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadSafeRandom {

    @UnknownNullability
    public static <T> T randomElement(@NotNull List<@Nullable T> list) {
        Objects.requireNonNull(list, "list");
        if (list.isEmpty()) {
            throw new NoSuchElementException("Can't get random element from empty list");
        }
        return list.get(randomInt(list.size()));
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

    public static int randomInt(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int randomInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @NotNull
    public static String randomString() {
        return Integer.toString(ThreadSafeRandom.randomInt(0x100000, 0xFFFFFF + 1), 16);
    }

    @NotNull
    public static String randomString(@NotNull Predicate<String> rejectPredicate, int maxAttempts) {
        int leftAttempts = maxAttempts;

        String result;
        do {
            if (leftAttempts <= 0) {
                throw new IllegalStateException("Can't create unique random string in %d attempts".formatted(maxAttempts));
            }

            result = randomString();
            leftAttempts--;
        } while (rejectPredicate.test(result));
        return result;
    }
}

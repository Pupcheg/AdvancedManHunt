package me.supcheg.advancedmanhunt.util;

import com.google.common.collect.Iterators;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OtherCollections {
    @NotNull
    @UnmodifiableView
    @Contract("_, _ -> new")
    public static <T> Collection<T> concat(@NotNull Collection<? extends T> first, @NotNull Collection<? extends T> second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        return new ConcatenatedUnmodifiableCollection<>(first, second);
    }

    @NotNull
    @UnmodifiableView
    @Contract(value = "_ -> new", pure = true)
    public static <T> Collection<T> concat(@NotNull Iterable<? extends Collection<T>> collectionsIterable) {
        Objects.requireNonNull(collectionsIterable, "collectionsIterable");

        Iterator<? extends Collection<T>> it = collectionsIterable.iterator();

        if (!it.hasNext()) {
            return java.util.Collections.emptySet();
        }

        Collection<T> collection = it.next();

        if (!it.hasNext()) {
            return java.util.Collections.unmodifiableCollection(collection);
        }

        while (it.hasNext()) {
            collection = concat(collection, it.next());
        }

        return collection;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class ConcatenatedUnmodifiableCollection<T> extends AbstractCollection<T> {
        private final Collection<? extends T> first;
        private final Collection<? extends T> second;

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return Iterators.concat(first.iterator(), second.iterator());
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            first.forEach(action);
            second.forEach(action);
        }

        @NotNull
        @Override
        public Stream<T> stream() {
            return Stream.concat(first.stream(), second.stream());
        }

        @NotNull
        @Override
        public Stream<T> parallelStream() {
            return Stream.concat(first.parallelStream(), second.parallelStream());
        }

        @Override
        public int size() {
            return first.size() + second.size();
        }

        @Override
        public boolean isEmpty() {
            return first.isEmpty() && second.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return first.contains(o) || second.contains(o);
        }

        @Override
        public boolean remove(Object o) {
            throw uoe();
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends T> c) {
            Objects.requireNonNull(c);
            throw uoe();
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            Objects.requireNonNull(c);
            throw uoe();
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            Objects.requireNonNull(filter);
            throw uoe();
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            Objects.requireNonNull(c);
            throw uoe();
        }

        @Override
        public void clear() {
            throw uoe();
        }

        @NotNull
        @Contract(value = " -> new", pure = true)
        private static UnsupportedOperationException uoe() {
            return new UnsupportedOperationException("This collection is unmodifiable");
        }
    }
}

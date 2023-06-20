package me.supcheg.advancedmanhunt.util;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ConcatenatedUnmodifiableCollection<T> extends AbstractCollection<T> {

    private final Collection<? extends T> firstDelegate;
    private final Collection<? extends T> secondDelegate;

    private ConcatenatedUnmodifiableCollection(Collection<? extends T> firstDelegate, Collection<? extends T> secondDelegate) {
        this.firstDelegate = firstDelegate;
        this.secondDelegate = secondDelegate;
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <T> Collection<T> of(@NotNull Collection<? extends T> firstDelegate, @NotNull Collection<? extends T> secondDelegate) {
        Objects.requireNonNull(firstDelegate);
        Objects.requireNonNull(secondDelegate);
        return new ConcatenatedUnmodifiableCollection<>(firstDelegate, secondDelegate);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.concat(firstDelegate.iterator(), secondDelegate.iterator());
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        firstDelegate.forEach(action);
        secondDelegate.forEach(action);
    }

    @Override
    public Stream<T> stream() {
        return Stream.concat(firstDelegate.stream(), secondDelegate.stream());
    }

    @Override
    public Stream<T> parallelStream() {
        return Stream.concat(firstDelegate.parallelStream(), secondDelegate.parallelStream());
    }

    @Override
    public int size() {
        return firstDelegate.size() + secondDelegate.size();
    }

    @Override
    public boolean isEmpty() {
        return firstDelegate.isEmpty() && secondDelegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return firstDelegate.contains(o) || secondDelegate.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}

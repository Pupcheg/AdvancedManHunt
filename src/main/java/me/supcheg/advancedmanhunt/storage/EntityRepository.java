package me.supcheg.advancedmanhunt.storage;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Closeable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public interface EntityRepository<E, K> extends Closeable {

    @CanIgnoreReturnValue
    boolean storeEntity(@NotNull E entity);

    @CanIgnoreReturnValue
    boolean invalidateKey(@NotNull K key);

    @CanIgnoreReturnValue
    default boolean invalidateEntity(@NotNull E entity) {
        return invalidateKey(getKey(entity));
    }

    @CanIgnoreReturnValue
    boolean containsKey(@NotNull K key);

    @CanIgnoreReturnValue
    default boolean containsEntity(@NotNull E entity) {
        return containsKey(getKey(entity));
    }

    @Nullable
    E getEntity(@NotNull K key);

    @NotNull
    E getOrCreateEntity(@NotNull K key, @NotNull Function<K, E> function);

    @NotNull
    @UnmodifiableView
    Map<K, E> getKeyToEntity();

    @NotNull
    @UnmodifiableView
    default Set<K> getKeys() {
        return getKeyToEntity().keySet();
    }

    @NotNull
    @UnmodifiableView
    default Collection<E> getEntities() {
        return getKeyToEntity().values();
    }

    @NotNull
    K getKey(@NotNull E entity);

    default void save() {
    }

    @Override
    default void close() {
        save();
    }
}

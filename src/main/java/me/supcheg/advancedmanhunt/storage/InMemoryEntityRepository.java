package me.supcheg.advancedmanhunt.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InMemoryEntityRepository<E, K> implements EntityRepository<E, K> {
    protected final Function<E, K> entity2key;
    protected final Map<K, E> entities;
    protected final Map<K, E> unmodifiableEntities;

    protected InMemoryEntityRepository(@NotNull Function<E, K> entity2key) {
        this.entity2key = entity2key;
        this.entities = new HashMap<>();
        this.unmodifiableEntities = Collections.unmodifiableMap(entities);
    }

    @Override
    public boolean storeEntity(@NotNull E entity) {
        return entities.putIfAbsent(getKey(entity), entity) == entity;
    }

    @Override
    public boolean invalidateKey(@NotNull K key) {
        return entities.remove(key) != null;
    }

    @Override
    public boolean containsKey(@NotNull K key) {
        return entities.containsKey(key);
    }

    @Nullable
    @Override
    public E getEntity(@NotNull K key) {
        return entities.get(key);
    }

    @NotNull
    @Override
    public E getOrCreateEntity(@NotNull K key, @NotNull Function<K, E> function) {
        return entities.computeIfAbsent(key, function);
    }

    @NotNull
    @UnmodifiableView
    @Override
    public Map<K, E> getKeyToEntity() {
        return unmodifiableEntities;
    }

    @NotNull
    @Override
    public K getKey(@NotNull E entity) {
        return entity2key.apply(entity);
    }
}
